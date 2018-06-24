package com.convrt.service;

import com.convrt.entity.User;
import com.convrt.entity.Video;
import com.convrt.repository.VideoRepository;
import com.convrt.view.VideoStreamMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
        video.setUuid(UUID.nameUUIDFromBytes(videoId.getBytes()).toString());
        video.setTitle(videoStreamMetadata.getTitle());
        video.setOwner(videoStreamMetadata.getOwner());
        video.setPlayDuration(videoStreamMetadata.getDuration());
        video.setStreamUrl(videoStreamMetadata.getSource());
        video.setStreamUrlDate(Instant.now());
        video.setVideoId(videoId);
        video.setStreamUrlExpireDate(videoStreamMetadata.getSourceExpireDate());
        return videoRepository.save(video);
    }

    @Transactional(readOnly = true)
    public VideoStreamMetadata readVideoByVideoId(String videoId) {
        Video video = videoRepository.findByVideoId(videoId);
        if (video == null) {
            return null;
        }
        Instant expireDate = video.getStreamUrlExpireDate();
        if (Instant.now().isBefore(expireDate)) {
            VideoStreamMetadata videoStreamMetadata = new VideoStreamMetadata();
            videoStreamMetadata.setSource(video.getStreamUrl());
            videoStreamMetadata.setSourceExpireDate(video.getStreamUrlExpireDate());
            videoStreamMetadata.setSourceFetchedDate(video.getStreamUrlDate());
            videoStreamMetadata.setVideoId(video.getVideoId());
            videoStreamMetadata.setDuration(video.getPlayDuration());
            videoStreamMetadata.setOwner(video.getOwner());
            videoStreamMetadata.setTitle(video.getTitle());
            return videoStreamMetadata;
        }
        return null;
    }

}
