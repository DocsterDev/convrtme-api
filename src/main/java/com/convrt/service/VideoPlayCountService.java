package com.convrt.service;

import com.convrt.entity.VideoPlayCount;
import com.convrt.repository.VideoPlayCountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Slf4j
@Service
public class VideoPlayCountService {

    @Autowired
    private VideoPlayCountRepository videoPlayCountRepository;

    @Transactional
    public VideoPlayCount iteratePlayCount(String userUuid, String videoId) {
        VideoPlayCount videoPlayCount = videoPlayCountRepository.findByUserUuidAndVideoId(userUuid, videoId);
        if (videoPlayCount == null) {
            videoPlayCount = new VideoPlayCount();
            videoPlayCount.setUuid(UUID.randomUUID().toString());
            videoPlayCount.setVideoId(videoId);
            videoPlayCount.setUserUuid(userUuid);
        }
        long playCount = videoPlayCount.getPlayCount();
        playCount += 1;
        videoPlayCount.setPlayCount(playCount);
        return videoPlayCountRepository.save(videoPlayCount);
    }

    @Transactional
    public long readPlayCountByVideoId(String userUuid, String videoId) {
        return videoPlayCountRepository.findByUserUuidAndVideoId(userUuid, videoId).getPlayCount();
    }

}
