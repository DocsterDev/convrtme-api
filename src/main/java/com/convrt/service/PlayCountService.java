package com.convrt.service;

import com.convrt.entity.PlayCount;
import com.convrt.repository.PlayCountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Slf4j
@Service
public class PlayCountService {

    @Autowired
    private PlayCountRepository playCountRepository;

    @Transactional
    public PlayCount iteratePlayCount(String userUuid, String videoId) {
        PlayCount playCount = playCountRepository.findByVideoId(videoId);
        if (playCount == null) {
            playCount = new PlayCount();
            playCount.setUuid(UUID.randomUUID().toString());
            playCount.setVideoId(videoId);
            playCount.setUserUuid(userUuid);
        }
        playCount.iterateNumPlays();
        return playCount;
    }

    @Transactional
    public Long readNumPlaysByVideoId(String videoId) {
        return playCountRepository.findByVideoId(videoId).getNumPlays();
    }

}
