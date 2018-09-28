package com.convrt.api.service;

import com.convrt.api.entity.Context;
import com.convrt.api.entity.User;
import com.convrt.api.entity.Video;
import com.convrt.api.repository.VideoRepository;
import com.convrt.api.view.Status;
import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
        videoPersistent.setOwner(video.getOwner());
        videoPersistent.setDuration(video.getDuration());
        return videoService.createOrUpdateVideo(video);
    }

    @Transactional(readOnly = true)
    public Status validateStreamUrl(String videoId) {
       return new Status(Objects.nonNull(videoService.readVideoMetadata(videoId)));
    }

    @Transactional
    public Video fetchStreamUrl(String videoId, String token) {
        if (videoId == null) {
            throw new RuntimeException("Cannot retrieve streamUrl when videoId is null.");
        }
        Video videoPersistent = videoRepository.findById(videoId);
        if (videoPersistent == null) {
            videoPersistent = new Video();
            videoPersistent.setId(videoId);
        }
        if (videoPersistent.getStreamUrl() == null || Instant.now().isAfter(videoPersistent.getStreamUrlExpireDate())) {
            log.info("Fetching new stream URL for videoId {}", videoId);
            videoPersistent.setStreamUrl(getStreamUrl(videoId));
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
        return video;
    }

    private String getStreamUrl(String videoId) {
        log.info("Attempting to fetch existing valid stream url for video={}", videoId);
        try {
            final AtomicBoolean stop = new AtomicBoolean(false);
            URL web = new URL(String.format(YOUTUBE_URL, videoId));
            VGetParser user = VGet.parser(web);
            VideoInfo videoinfo = user.info(web);
            new VGet(videoinfo).extract(user, stop, () -> {
            });
            List<VideoFileInfo> list = videoinfo.getInfo();
            return findAudioStreamUrl(list);
        } catch (NullPointerException e) {
            throw new RuntimeException("Sorry bruh, looks like we couldn't find this video!", e);
        } catch (Exception e) {
            throw new RuntimeException("Oops, looks like something went wrong :(", e);
        }
    }

    private String findAudioStreamUrl(List<VideoFileInfo> list) {
        VideoFileInfo videoFileInfo = null;
        if (list != null) {
            for (VideoFileInfo d : list) {
                log.info("Found content-type: " + d.getContentType());
                if (d.getContentType().contains("audio")) {
                    log.info("Dedicated audio url found");
                    return d.getSource().toString();
                }
                videoFileInfo = d;
            }
            log.info("No dedicated audio url found. Returning full video url.");
            return videoFileInfo.getSource().toString();
        }
        throw new RuntimeException("Could not extract media stream url.");
    }

}
