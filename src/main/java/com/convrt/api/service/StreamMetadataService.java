package com.convrt.api.service;

import com.convrt.api.entity.Context;
import com.convrt.api.entity.User;
import com.convrt.api.entity.Video;
import com.convrt.api.repository.VideoRepository;
import com.convrt.api.view.Status;
import com.convrt.api.view.VideoWS;
import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class StreamMetadataService {

    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private ContextService contextService;

    static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=%s";

    @Transactional
    public Video updateVideoMetadata(Video video) {
        Video videoPersistent = videoService.readVideoByVideoId(video.getId());
        if (videoPersistent == null) {
            videoPersistent = new Video();
            videoPersistent.setId(UUID.randomUUID().toString());
        }
        videoPersistent.setTitle(video.getTitle());
        videoPersistent.setChannel(video.getChannel());
        videoPersistent.setDuration(video.getDuration());
        return videoService.createOrUpdateVideo(video);
    }

    @Transactional(readOnly = true)
    public Status validateStreamUrl(String videoId) {
       return new Status(Objects.nonNull(videoService.readVideoMetadata(videoId)));
    }

    public void prefetchStreamUrl(String videoId) {
        log.info("Pre-fetching stream URL for video id {}", videoId);
        fetchStreamUrl(videoId, null);
    }

    @Transactional
    public VideoWS fetchStreamUrl(String videoId, String token) {
        if (videoId == null) {
            throw new RuntimeException("Cannot retrieve streamUrl when videoId is null.");
        }
        Video videoPersistent = videoRepository.findById(videoId);
        if (videoPersistent == null) {
            videoPersistent = new Video();
            videoPersistent.setId(videoId);
        }
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
        Video video = videoService.createOrUpdateVideo(videoPersistent);
        if (token != null) {
            Context context = contextService.validateContext(token);
            if (context != null) {
                User user = context.getUser();
                user.getVideos().add(video);
            }
        }
        VideoWS videoWS = new VideoWS();
        videoWS.setStreamUrl(videoPersistent.getStreamUrl());
        videoWS.setAudioOnly(BooleanUtils.toBoolean(videoPersistent.getAudioOnly()));
        return videoWS;
    }

    private void getStreamUrlFromVGet(String videoId, Video video) {
        log.info("Attempting to fetch existing valid stream url for video={}", videoId);
        try {
            final AtomicBoolean stop = new AtomicBoolean(false);
            URL web = new URL(String.format(YOUTUBE_URL, videoId));
            VGetParser user = VGet.parser(web);
            VideoInfo videoinfo = user.info(web);
            new VGet(videoinfo).extract(user, stop, () -> {
            });
            List<VideoFileInfo> list = videoinfo.getInfo();
            findAudioStreamUrl(list, video);
        } catch (NullPointerException e) {
            throw new RuntimeException("Sorry bruh, looks like we couldn't find this video!", e);
        } catch (Exception e) {
            throw new RuntimeException("Oops, looks like something went wrong :(", e);
        }
    }

    private void findAudioStreamUrl(List<VideoFileInfo> list, Video video) {
        VideoFileInfo videoFileInfo = null;
        if (list != null) {
            for (VideoFileInfo d : list) {
                log.info("Found content-type: " + d.getContentType());
                if (d.getContentType().contains("audio")) {
                    log.info("Dedicated audio url found");
                    video.setAudioOnly(true);
                    video.setStreamUrl(d.getSource().toString());
                    return;
                }
                videoFileInfo = d;
            }
            log.info("No dedicated audio url found. Returning full video url.");
            video.setAudioOnly(false);
            video.setStreamUrl(videoFileInfo.getSource().toString());
            return;
        }
        throw new RuntimeException("Could not extract media stream url.");
    }

    private void getStreamUrlFromYouTubeDL(String videoId, Video video){
        ProcessBuilder pb = extractorProcess(videoId);
        try {
            Process p = pb.start();
            try (InputStream is = p.getInputStream(); InputStream es = p.getErrorStream();) {
                String error = IOUtils.toString(es, "UTF-8");
                String output = IOUtils.toString(is, "UTF-8");
                if (StringUtils.isBlank(output) && StringUtils.isNotBlank(error)) {
                    throw new RuntimeException(String.format("Extracted stream URL is null for video id %s: %s", videoId, error));
                }
                String[] urlArray = StringUtils.split(output, "\r\n");
                boolean audioStreamExist = (urlArray.length > 1);
                video.setStreamUrl(audioStreamExist ? urlArray[1] : urlArray[0]);
                video.setAudioOnly(audioStreamExist);
                return;
            } catch (IOException e) {
                throw new RuntimeException(String.format("Error executing YouTube-DL to extract video id for %s", videoId), e);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error starting process to extract url", videoId), e);
        }
    }

    private ProcessBuilder extractorProcess(String videoId) {
        return new ProcessBuilder("youtube-dl",
                "--quiet",
                "--simulate",
                "--get-url",
                "--",
                videoId
        );
    }
}
