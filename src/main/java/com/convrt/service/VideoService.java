package com.convrt.service;

import com.convrt.entity.Video;
import com.convrt.repository.VideoRepository;
import com.convrt.view.VideoStreamMetadata;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private PlayCountService playCountService;

    @Transactional
    public Video createVideo(VideoStreamMetadata videoStreamMetadata) {
        String videoId = videoStreamMetadata.getVideoId();
        Video video = new Video();
        video.setId(videoStreamMetadata.getVideoId());
        video.setTitle(videoStreamMetadata.getTitle());
        video.setOwner(videoStreamMetadata.getOwner());
        video.setPlayDuration(videoStreamMetadata.getDuration());
        video.setStreamUrl(videoStreamMetadata.getSource());
        video.setStreamUrlDate(Instant.now());
        video.setStreamUrlExpireDate(videoStreamMetadata.getSourceExpireDate());
        return videoRepository.save(video);
    }

    @Transactional
    public void createAllVideos(List<Video> videoList) {
        List<Video> videos = Lists.newArrayList();
        videoList.forEach((v) -> {
            if (!existsByVideoId(v.getId())) {
                Video video = new Video();
                video.setId(v.getId());
                video.setTitle(v.getTitle());
                video.setOwner(v.getOwner());
                video.setPlayDuration(v.getPlayDuration());
                videos.add(video);
            }
        });
        videoRepository.save(videos);
    }

    @Transactional(readOnly = true)
    public VideoStreamMetadata readVideoMetadata(String id) {
        Video video = videoRepository.findByIdAndStreamUrlExpireDateNotNull(id);
        if (video == null) {
            return null;
        }
        Instant expireDate = video.getStreamUrlExpireDate();
        if (Instant.now().isBefore(expireDate)) {
            VideoStreamMetadata videoStreamMetadata = new VideoStreamMetadata();
            videoStreamMetadata.setSource(video.getStreamUrl());
            videoStreamMetadata.setSourceExpireDate(expireDate);
            videoStreamMetadata.setSourceFetchedDate(video.getStreamUrlDate());
            videoStreamMetadata.setVideoId(video.getId());
            videoStreamMetadata.setDuration(video.getPlayDuration());
            videoStreamMetadata.setOwner(video.getOwner());
            videoStreamMetadata.setTitle(video.getTitle());
            return videoStreamMetadata;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Video readVideo(String id) {
        Video video = videoRepository.findOne(id);
        if (video == null) {
            throw new RuntimeException(String.format("Cannot find video with video id %s. Video must exist first.", id));
        }
        return video;
    }

    @Transactional(readOnly = true)
    public boolean existsByVideoId(String videoId) {
        return videoRepository.exists(videoId);
    }

}
