package com.moup.api.service;

import com.moup.api.entity.Channel;
import com.moup.api.entity.Context;
import com.moup.api.entity.User;
import com.moup.api.entity.UserVideo;
import com.moup.api.entity.Video;
import com.moup.api.repository.UserVideoRepository;
import com.moup.api.repository.VideoRepository;
import com.moup.api.utils.UUIDUtils;
import com.moup.api.view.View;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private ContextService contextService;
    @Autowired
    private UserVideoRepository userVideoRepository;

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
        videoRepository.saveAll(videos);
        sw.stop();
        log.info("Took {}ms to save videos", sw.getTotalTimeMillis());
    }

    @Transactional
    public Video updateVideo(Video video) {
        Optional<Video> videoPersistent = videoRepository.findById(video.getId());
        if (!videoPersistent.isPresent()) {
            throw new RuntimeException(String.format("No video found to update: video id %s", video.getId()));
        }
        return videoRepository.save(video);
    }

    @Transactional(readOnly = true)
    public Video readVideo(String id) {
        Optional<Video> video = videoRepository.findById(id);
        if (!video.isPresent()) {
            throw new RuntimeException(String.format("Cannot find video with video id %s. Video must exist first.", id));
        }
        return video.get();
    }

    @Transactional(readOnly=true)
    public List<Video> findVideosByChannel(Channel channel, Instant subscribedDate){
       return videoRepository.findVideosByChannelAndSubscriptionScannedDateIsAfter(channel, subscribedDate);
    }

    @JsonView(View.VideoWithPlaylist.class)
    @Transactional(readOnly = true)
    public Video readVideoByVideoId(String id) {
        Optional<Video> video = videoRepository.findById(id);
        if (!video.isPresent()) {
            throw new RuntimeException(String.format("Cannot find video with video id %s. Video must exist first.", id));
        }
        return video.get();
    }

    @Transactional(readOnly = true)
    public boolean existsByVideoId(String videoId) {
        return videoRepository.existsById(videoId);
    }

    @Async
    @Transactional
    public Video updateVideoWatched(String videoId, String token) {
        if (token == null) {
            return null;
        }
        // TODO - Add Temporal auto generated date column to user_video table
        Optional<Video> videoPersistent = videoRepository.findById(videoId);
        Video video = null;
        if (!videoPersistent.isPresent()) {
            video = new Video();
            video.setId(videoId);
            video = videoRepository.save(video);
        } else {



            video = videoPersistent.get();
        }
        Context context = contextService.validateContext(token);
        if (context != null) {
            User user = context.getUser();
            UserVideo userVideoPersistent = userVideoRepository.findFirstByUserUuidOrderByVideosOrderDesc(user.getUuid());
            int count = 0;
            if (userVideoPersistent != null) {
                count = userVideoPersistent.getVideosOrder();
                log.info("Current user video count: {}", count);
                count++;
            }
            UserVideo userVideo = new UserVideo();
            userVideo.setUuid(UUIDUtils.generateUuid(user.getUuid() + count));
            userVideo.setUser(user);
            userVideo.setVideo(video);
            userVideo.setVideosOrder(count);
            userVideo.setViewedDate(Instant.now());
            userVideo.setPlayheadPosition(0L);
            userVideoRepository.save(userVideo);
            log.info("Updating video as watch for user");
            return video;
        }
        log.error("Unable to update video as watched. No user context found.");
        return null;
    }

    @Async
    @Transactional
    public void updatePlayheadPosition(String token, String videoId, Long position) {
        Context context = contextService.validateContext(token);
        if (context != null) {
            User user = context.getUser();
            UserVideo userVideoPersistent = userVideoRepository.findFirstByUserUuidAndVideoIdOrderByVideosOrderDesc(user.getUuid(), videoId);
            if (Objects.nonNull(userVideoPersistent)) {
                userVideoPersistent.setPlayheadPosition(position);
            }
            log.info("Successfully updated video id {} to position {}", videoId, position);
            return;
        }
        log.error("Unable to update video as watched. No user context found.");
        return;
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
