package com.convrt.service;

import com.convrt.entity.Video;
import com.convrt.entity.VideoPlayCount;
import com.convrt.repository.VideoPlayCountRepository;
import com.convrt.repository.VideoRepository;
import com.convrt.view.VideoInfoWS;
import com.convrt.view.VideoStreamInfoWS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
public class VideoService {


    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private VideoPlayCountService videoPlayCountService;


    @Transactional
    public Video createVideo(String userUuid, VideoStreamInfoWS streamInfo) {
        String videoId = streamInfo.getVideoInfo().getId();
        Video video = new Video();
        video.setUuid(UUID.nameUUIDFromBytes(videoId.getBytes()).toString());
        video.setTitle(streamInfo.getVideoInfo().getTitle());
        video.setOwner(streamInfo.getVideoInfo().getOwner());
        video.setPublishedTimeAgo(streamInfo.getVideoInfo().getPublishedTimeAgo());
        video.setPlayDuration(streamInfo.getVideoInfo().getDuration());
        video.setStreamUrl(streamInfo.getSource());
        video.setDataSize(streamInfo.getSize());
        video.setStreamUrlDate(Instant.now());
        video.setAudioOnly(streamInfo.isAudio());
        video.setVideoId(videoId);
        video.setStreamUrlExpireDate(streamInfo.getSourceExpireDate());
        return videoRepository.save(video);
    }

    @Transactional
    public VideoStreamInfoWS readVideoByVideoId(String userUuid, String videoId) {
        Video video = videoRepository.findByVideoId(videoId);
        if (video == null) {
            return null;
        }
        Instant expireDate = video.getStreamUrlExpireDate();
        if (Instant.now().isBefore(expireDate)) {
            VideoStreamInfoWS videoStreamInfo = new VideoStreamInfoWS();
            VideoInfoWS videoInfoWS = new VideoInfoWS();
            videoStreamInfo.setVideoInfo(videoInfoWS);
            videoStreamInfo.setSource(video.getStreamUrl());
            videoStreamInfo.setSize(video.getDataSize());
            videoStreamInfo.setAudio(video.isAudioOnly());
            videoStreamInfo.setSourceExpireDate(video.getStreamUrlExpireDate());
            videoStreamInfo.setSourceFetchedDate(video.getStreamUrlDate());
            videoInfoWS.setId(video.getVideoId());
            videoInfoWS.setDuration(video.getPlayDuration());
            videoInfoWS.setOwner(video.getOwner());
            videoInfoWS.setTitle(video.getTitle());
            videoInfoWS.setPublishedTimeAgo(videoInfoWS.getPublishedTimeAgo());
            videoInfoWS.setViewCount(video.getViewCount());
            long playCount = videoPlayCountService.readPlayCountByVideoId(userUuid, videoId);
            videoInfoWS.setPlayCount(playCount);

            return videoStreamInfo;
        }
        return null;
    }

}
