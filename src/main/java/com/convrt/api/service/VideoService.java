package com.convrt.api.service;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Context;
import com.convrt.api.entity.User;
import com.convrt.api.entity.Video;
import com.convrt.api.repository.VideoRepository;
import com.convrt.api.view.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private ContextService contextService;

    @Transactional
    public Video createOrUpdateVideo(Video video) {
        return videoRepository.save(video);
    }

    @Transactional
    @Async
    public void createAllVideos(List<Video> videoList) {
        StopWatch sw = new StopWatch();
        sw.start();
        List<Video> videos = Lists.newArrayList();
        videoList.forEach((v) -> {
            if (!existsByVideoId(v.getId())) {
                Video video = new Video();
                video.setId(v.getId());
                video.setTitle(v.getTitle());
                video.setChannel(v.getChannel());
                video.setDuration(v.getDuration());
                videos.add(video);
            }
        });
        videoRepository.save(videos);
        sw.stop();
        log.info("Took {}ms to save videos", sw.getTotalTimeMillis());
    }

    /*
    @Transactional(readOnly = true)
    public Video readVideoMetadata(String id) {
        Video video = videoRepository.findByIdAndStreamUrlExpireDateNotNull(id);
        if (video == null) {
            return null;
        }
        Instant expireDate = video.getStreamUrlExpireDate();
        if (Instant.now().isBefore(expireDate)) {
            return video;
        }
        return null;
    }
    */

    @Transactional
    public Video updateVideo(Video video) {
        Video videoPersistent = videoRepository.findById(video.getId());
        if (videoPersistent == null) {
            throw new RuntimeException(String.format("No video found to update: video id %s", video.getId()));
        }
        return videoRepository.save(video);
    }

    @Transactional(readOnly = true)
    public Video readVideo(String id) {
        Video video = videoRepository.findOne(id);
        if (video == null) {
            throw new RuntimeException(String.format("Cannot find video with video id %s. Video must exist first.", id));
        }
        return video;
    }

    @Transactional(readOnly=true)
    public List<Video> findVideosByChannel(Channel channel, Instant subscribedDate){
       return videoRepository.findVideosByChannelAndSubscriptionScannedDateIsAfter(channel, subscribedDate);
    }

    @JsonView(View.VideoWithPlaylist.class)
    @Transactional(readOnly = true)
    public Video readVideoByVideoId(String id) {
        return videoRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByVideoId(String videoId) {
        return videoRepository.exists(videoId);
    }

    @Async
    @Transactional
    public Video updateVideoWatched(String videoId, String token) {
        Video videoPersistent = videoRepository.findById(videoId);
        if (videoPersistent == null) {
            videoPersistent = new Video();
            videoPersistent.setId(videoId);
        }
        Video video = createOrUpdateVideo(videoPersistent);
        if (token != null) {
            Context context = contextService.validateContext(token);
            if (context != null) {
                User user = context.getUser();
                user.getVideos().add(video);
            }
            log.info("Updating video as watch for user");
        }
        return videoPersistent;
    }

    @Transactional
    public Video updateVideoMetadata(Video video) {
        Video videoPersistent = readVideoByVideoId(video.getId());
        if (videoPersistent == null) {
            videoPersistent = new Video();
            videoPersistent.setId(UUID.randomUUID().toString());
        }
        videoPersistent.setTitle(video.getTitle());
        videoPersistent.setChannel(video.getChannel());
        videoPersistent.setDuration(video.getDuration());
        return createOrUpdateVideo(video);
    }

}
