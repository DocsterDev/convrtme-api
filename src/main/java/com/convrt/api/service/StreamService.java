package com.convrt.api.service;

import com.convrt.api.entity.Stream;
import com.convrt.api.entity.Video;
import com.convrt.api.repository.StreamRepository;
import com.convrt.api.repository.VideoRepository;
import com.convrt.api.utils.URLUtils;
import com.convrt.api.utils.UUIDUtils;
import com.convrt.api.view.Status;
import com.convrt.api.view.StreamWS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class StreamService {

    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private ContextService contextService;
    @Autowired
    private StreamRepository streamRepository;
    @Autowired
    private AudioExtractorService audioExtractorService;
    @Autowired
    private ObjectMapper objectMapper;

    static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=%s";


//    @Transactional(readOnly = true)
//    public Status validateStreamUrl(String videoId) {
//       return new Status(Objects.nonNull(videoService.readVideoMetadata(videoId)));
//    }

//    public StreamWS prefetchStreamUrl(String videoId) {
//        log.info("Pre-fetching stream URL for video id {}", videoId);
//        return fetchStreamUrl(videoId, null);
//    }


    @Transactional
    public Stream readStream(String videoId, String extension) {
        return streamRepository.findByVideoIdAndExtension(videoId, extension);
    }

    @Transactional
    public Stream createOrUpdate(String videoId, String extension, String streamUrl) {
        Stream streamPersistent = readStream(videoId, extension);

        streamPersistent.setVideoId(videoId);
        streamPersistent.setExtension(extension);
        streamPersistent.setStreamUrl(streamUrl);
        return streamRepository.save(streamPersistent);
    }

    private StreamWS getStreamObject(String videoId, String extension){
        StreamWS vgetStream = getVGetStream(videoId);
        if (vgetStream.isSuccess() && vgetStream.isAudioOnly() && vgetStream.isMatchesExtension()) {
            return vgetStream;
        }
        StreamWS youtubeDlStream = getYoutubeDLStream(videoId, extension);
        if (!vgetStream.isSuccess() && !youtubeDlStream.isSuccess()) {
            return StreamWS.ERROR;
        }
        if (vgetStream.isSuccess() && !youtubeDlStream.isSuccess()) {
            return vgetStream;
        }
        if (!vgetStream.isSuccess() && youtubeDlStream.isSuccess()) {
            return youtubeDlStream;
        }
        if (youtubeDlStream.isAudioOnly()) {
            return youtubeDlStream;
        }
        return vgetStream;
    }

    @Transactional
    public StreamWS fetchStreamUrl(String videoId, String extension) {
        Stream streamPersistent = readStream(videoId, extension);
        boolean success = false;
        StreamWS gblStreamWS = null;
        if (Objects.isNull(streamPersistent) || streamPersistent.getStreamUrl() == null || Instant.now().isAfter(streamPersistent.getStreamUrlExpireDate())) {
            // Fork join the two calls
            streamPersistent = new Stream();
            streamPersistent.setUuid(UUIDUtils.generateUuid(videoId, extension));
            streamPersistent.setVideoId(videoId);
            streamPersistent.setExtension(extension);

            gblStreamWS = getStreamObject(videoId, extension);

            streamPersistent.setStreamUrl(gblStreamWS.getStreamUrl());
            streamPersistent.setSource(gblStreamWS.getSource());
            // Get both StreamWS and then figure out which one is better. If neither one finds a url then send back false success
            streamRepository.save(streamPersistent);
        }
        StreamWS streamWS = new StreamWS();
        streamWS.setId(streamPersistent.getVideoId());
        streamWS.setExtension(streamPersistent.getExtension());
        streamWS.setStreamUrl(streamPersistent.getStreamUrl());
        streamWS.setAudioOnly(streamPersistent.isAudioOnly());
        streamWS.setSource(streamPersistent.getSource());
        streamWS.setMatchesExtension(streamPersistent.isMatchesExtension());
        streamWS.setSuccess(StringUtils.isNotBlank(streamPersistent.getStreamUrl()));
        return streamWS;
    }

    public StreamWS getVGetStream(String videoId) {
        log.info("Attempting to fetch existing valid stream url for video={}", videoId);
        try {
            StreamWS streamWS = new StreamWS();
            final AtomicBoolean stop = new AtomicBoolean(false);
            URL web = new URL(String.format(YOUTUBE_URL, videoId));
            VGetParser user = VGet.parser(web);
            VideoInfo videoinfo = user.info(web);
            new VGet(videoinfo).extract(user, stop, () -> {
            });
            List<VideoFileInfo> list = videoinfo.getInfo();
            VideoFileInfo videoFileInfo = null;
            if (list != null) {
                for (VideoFileInfo d : list) {
                    log.info("Found content-type: " + d.getContentType());
                    if (d.getContentType().contains("audio")) {
                        log.info("Dedicated audio url found");
                        streamWS.setId(videoId);
                        String streamUrl = d.getSource().toString();
                        streamWS.setStreamUrl(streamUrl);
                        streamWS.setAudioOnly(URLUtils.isAudioOnly(streamUrl));
                        streamWS.setSuccess(true);
                        return streamWS;
                    }
                    videoFileInfo = d;
                }
                log.info("No dedicated audio url found. Returning full video url.");
                String streamUrl = videoFileInfo.getSource().toString();
                streamWS.setStreamUrl(streamUrl);
                streamWS.setAudioOnly(URLUtils.isAudioOnly(streamUrl));
                streamWS.setSuccess(true);
                return streamWS;
            }
            return StreamWS.ERROR;
        } catch (Exception e) {
            log.error("Error retrieving stream url video id {} from VGET", videoId, e);
            return StreamWS.ERROR;
        }
    }

    public StreamWS getYoutubeDLStream(String videoId, String extension){
        ProcessBuilder pb = audioExtractorService.buildProcess(videoId, extension);
        try {
            Process p = pb.start();
            try (InputStream is = p.getInputStream(); InputStream es = p.getErrorStream();) {
                String error = IOUtils.toString(es, "UTF-8");
                String output = IOUtils.toString(is, "UTF-8");
                if (StringUtils.isBlank(output) && StringUtils.isNotBlank(error)) {
                    log.error("Extracted stream URL is null for video id {}: {}", videoId, error);
                    return StreamWS.ERROR;
                }
                String[] urlArray = StringUtils.split(output, "\r\n");
                boolean audioStreamExist = (urlArray.length > 1);
                StreamWS streamWS = new StreamWS();
                streamWS.setId(videoId);
                String streamUrl = audioStreamExist ? urlArray[1] : urlArray[0];
                streamWS.setStreamUrl(streamUrl);
                streamWS.setAudioOnly(URLUtils.isAudioOnly(streamUrl));
                streamWS.setSuccess(true);
                return streamWS;
            } catch (IOException e) {
                log.error("Error executing YouTube-DL to extract video id for {}", videoId, e);
                return StreamWS.ERROR;
            }
        } catch (Exception e) {
            log.error("Error starting process to extract url for video id {}", videoId, e);
            return StreamWS.ERROR;
        }
    }

//    private ProcessBuilder extractorProcess(String videoId) {
//        return new ProcessBuilder("youtube-dl",
//                "--quiet",
//                "--simulate",
//                "--get-url",
//                "--",
//                videoId
//        );
//    }




    /*


    if (videoPersistent.getStreamUrl() == null || Instant.now().isAfter(videoPersistent.getStreamUrlExpireDate())) {
    log.info("Fetching new stream URL for video id {}", videoId);
    StopWatch totalTime = StopWatch.createStarted();
    try {
    StopWatch vgetTime = StopWatch.createStarted();
    getStreamUrlFromVGet(videoId, videoPersistent);
    log.info("Fetch Stream URL - VGET took {}ms", vgetTime.getTime(TimeUnit.MILLISECONDS));
    } catch (Exception e) {
    log.info("Fetch Stream URL - VGET failed after {}ms", totalTime.getTime(TimeUnit.MILLISECONDS));
    log.warn("Failed using VGET to fetch video stream url. Retrying with YouTube-DL.");
    StopWatch youtubeDLTime = StopWatch.createStarted();
    getStreamUrlFromYouTubeDL(videoId, videoPersistent);
    log.info("Fetch Stream URL - youtube-dl took {}ms", youtubeDLTime.getTime(TimeUnit.MILLISECONDS));
    }
    log.info("Fetch Stream URL - Total time took {}ms", totalTime.getTime(TimeUnit.MILLISECONDS));
    log.info("Successfully fetched video stream URL: {}", videoPersistent.getStreamUrl());
    } else {
    log.info("Stream URL already exists and is valid for videoId {}", videoId);
    }


    */

}
