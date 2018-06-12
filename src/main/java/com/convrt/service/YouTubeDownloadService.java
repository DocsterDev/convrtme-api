package com.convrt.service;

import com.convrt.view.VideoInfoWS;
import com.convrt.view.VideoStreamInfoWS;
import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.vhs.VimeoInfo;
import com.github.axet.vget.vhs.YouTubeInfo;
import com.github.axet.wget.SpeedInfo;
import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class YouTubeDownloadService {

    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoPlayCountService videoPlayCountService;
    @Autowired
    private YouTubeConversionService youTubeConversionService;

    static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=%s";

    static class VGetStatus implements Runnable {
        VideoInfo videoinfo;
        long last;

        Map<VideoFileInfo, SpeedInfo> map = Maps.newHashMap();

        public VGetStatus(VideoInfo i) {
            this.videoinfo = i;
        }

        public SpeedInfo getSpeedInfo(VideoFileInfo dinfo) {
            SpeedInfo speedInfo = map.get(dinfo);
            if (speedInfo == null) {
                speedInfo = new SpeedInfo();
                speedInfo.start(dinfo.getCount());
                map.put(dinfo, speedInfo);
            }
            return speedInfo;
        }

        @Override
        public void run() {
            List<VideoFileInfo> dinfoList = videoinfo.getInfo();

            // notify app or save download state
            // you can extract information from DownloadInfo info;
            switch (videoinfo.getState()) {
                case EXTRACTING:
                case EXTRACTING_DONE:
                case DONE:
                    if (videoinfo instanceof YouTubeInfo) {
                        YouTubeInfo i = (YouTubeInfo) videoinfo;
                        log.info(videoinfo.getState() + " " + i.getVideoQuality());
                    } else if (videoinfo instanceof VimeoInfo) {
                        VimeoInfo i = (VimeoInfo) videoinfo;
                        log.info(videoinfo.getState() + " " + i.getVideoQuality());
                    } else {
                        log.info("downloading unknown quality");
                    }
                    for (VideoFileInfo d : videoinfo.getInfo()) {
                        SpeedInfo speedInfo = getSpeedInfo(d);
                        speedInfo.end(d.getCount());
                        log.info(String.format("file:%d - %s (%s)", dinfoList.indexOf(d), d.targetFile,
                                formatSpeed(speedInfo.getAverageSpeed())));
                    }
                    break;
                case ERROR:
                    log.info(videoinfo.getState() + " " + videoinfo.getDelay());

                    if (dinfoList != null) {
                        for (DownloadInfo dinfo : dinfoList) {
                            log.info("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getException() + " delay:"
                                    + dinfo.getDelay());
                        }
                    }
                    break;
                case RETRYING:
                    log.info(videoinfo.getState() + " " + videoinfo.getDelay());

                    if (dinfoList != null) {
                        for (DownloadInfo dinfo : dinfoList) {
                            log.info("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getState() + " "
                                    + dinfo.getException() + " delay:" + dinfo.getDelay());
                        }
                    }
                    break;
                case DOWNLOADING:
                    long now = System.currentTimeMillis();
                    if (now - 1000 > last) {
                        last = now;

                        String parts = "";

                        for (VideoFileInfo dinfo : dinfoList) {
                            SpeedInfo speedInfo = getSpeedInfo(dinfo);
                            speedInfo.step(dinfo.getCount());

                            List<DownloadInfo.Part> pp = dinfo.getParts();
                            if (pp != null) {
                                // multipart download
                                for (Part p : pp) {
                                    if (p.getState().equals(VideoInfo.States.DOWNLOADING)) {
                                        parts += String.format("part#%d(%.2f) ", p.getNumber(),
                                                p.getCount() / (float) p.getLength());
                                    }
                                }
                            }
                            log.info(String.format("file:%d - %s %.2f %s (%s)", dinfoList.indexOf(dinfo),
                                    videoinfo.getState(), dinfo.getCount() / (float) dinfo.getLength(), parts,
                                    formatSpeed(speedInfo.getCurrentSpeed())));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static String formatSpeed(long s) {
        if (s > 0.1 * 1024 * 1024 * 1024) {
            float f = s / 1024f / 1024f / 1024f;
            return String.format("%.1f GB/s", f);
        } else if (s > 0.1 * 1024 * 1024) {
            float f = s / 1024f / 1024f;
            return String.format("%.1f MB/s", f);
        } else {
            float f = s / 1024f;
            return String.format("%.1f kb/s", f);
        }
    }

    private VideoStreamInfoWS startDownload(String videoId) {
        File path = new File("youtube-download");

        try {
            final AtomicBoolean stop = new AtomicBoolean(false);

            URL web = new URL(String.format(YOUTUBE_URL, videoId));
            VGetParser user = null;
            // create proper html parser depends on url
            user = VGet.parser(web);

            // user = new YouTubeQParser(YouTubeInfo.YoutubeQuality.p480);

            // download limited video quality from youtube
           // user = new YouTubeQParser(YouTubeInfo.YoutubeQuality.p480);
            VideoInfo videoinfo = user.info(web);
            VGet v = new VGet(videoinfo, path);
            VGetStatus notify = new VGetStatus(videoinfo);
            v.extract(user, stop, notify);
            List<VideoFileInfo> list = videoinfo.getInfo();
            VideoFileInfo videoFileInfo = null;
            // DEVELOPMENT ONLY
            log.info("Video Size: ");
            Long videoSize;
            Long audioSize;
            // DEV ONLY
            if (list != null) {
                log.info("Scanning content-types");
                for (VideoFileInfo d : list) {
                    log.info("Found content-type: " + d.getContentType());
                    log.info("One of the sizes: " + d.getLength());
                    if (d.getContentType().contains("audio")) {
                        log.info("Dedicated audio url found");

                        log.info("Audio Size (Should be smaller): " + d.getLength());
                        return new VideoStreamInfoWS(d.getSource().toString(), d.getLength(), d.getContentType(), true);
                    }
                    videoFileInfo = d;
                }
                log.info("No dedicated audio url found. Returning full video url.");
                if (list.size() > 1) {
                    throw new RuntimeException(String.format("More than one file found for videoId %s", videoId));
                }
                return new VideoStreamInfoWS(videoFileInfo.getSource().toString(), videoFileInfo.getLength(), videoFileInfo.getContentType(), false);
            }
            throw new RuntimeException("Sorry Bro, looks like we couldn't find this video!");
            // v.download(user, stop, notify);
        } catch (NullPointerException e) {
            throw new RuntimeException("Sorry Bro, looks like we couldn't find this video!", e);
        } catch (Exception e) {
            throw new RuntimeException("Oops, looks like something went wrong :(", e);
        }
    }

    @Cacheable("video")
    public VideoStreamInfoWS downloadAndSaveVideo(String userUuid, VideoInfoWS videoInfo) {
        log.info("Attempting to fetch existing valid stream url for video={} user={}", videoInfo.getVideoId(), userUuid);
        VideoStreamInfoWS streamInfo = videoService.readVideoByVideoId(userUuid, videoInfo.getVideoId());
        if (streamInfo == null) {
            log.info("No existing stream url available for video={} user={}", videoInfo.getVideoId(), userUuid);
            streamInfo = startDownload(videoInfo.getVideoId());
            streamInfo.setVideoInfo(videoInfo);
            videoService.createVideo(userUuid, streamInfo);
        } else {
            try {
                Thread.sleep(250);
            } catch (Exception e) {

            }
        }
        log.info("Attempting to open audio stream");

        youTubeConversionService.convertVideo();

        // captureByteStream(streamInfo);

        log.info("Opened audio stream");
        videoPlayCountService.iteratePlayCount(userUuid, videoInfo.getVideoId());
        return streamInfo;
    }

    private void captureByteStream(VideoStreamInfoWS streamInfo) {

        File outputFile = new File("test-output.webm");
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start tailer for output file to see if this shit works
        Thread thread = new Thread(() -> {

            TailerListener listener = new TailerListener() {
                @Override
                public void handle(String line) {
                   // System.out.println(line);
                    if (line != null) {
                        log.info("Byte size: " + line.getBytes().length);
                    }
                }

                @Override
                public void handle(Exception e) {
                    System.out.println(e);
                }

                @Override
                public void init(Tailer tailer) {
                    System.out.println("Init");
                }

                @Override
                public void fileNotFound() {

                }

                @Override
                public void fileRotated() {

                }
            };

            Tailer tailer = Tailer.create(outputFile, listener, 2000);
            tailer.run();

        });
        //thread.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        URL url = null;

        FileOutputStream fileoutputstream = null;
        try {
            fileoutputstream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found for streaming");
        }

        try {
            url = new URL(streamInfo.getSource());
            is = url.openStream ();
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ( (n = is.read(byteChunk)) > 0 ) {
                byte[] byteArray = byteChunk;
                // baos.write(byteArray, 0, n);
                fileoutputstream.write(byteArray);
                log.info("File size: " + outputFile.length());
                log.info("Byte Array Size:" + baos.size());
            }
        }
        catch (IOException e) {
            System.err.printf ("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
            e.printStackTrace ();
            // Perform any other exception handling that's appropriate.
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                    fileoutputstream.close();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }
    }
}
