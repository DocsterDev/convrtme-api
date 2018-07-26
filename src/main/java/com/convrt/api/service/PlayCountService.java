package com.convrt.api.service;

import com.convrt.api.entity.Video;
import com.convrt.api.repository.PlayCountRepository;
import com.convrt.api.entity.PlayCount;
import com.convrt.api.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PlayCountService {

    @Autowired
    private PlayCountRepository playCountRepository;

    @Transactional
    public Long iterateNumPlays(String videoId, String userId) {
        PlayCount playCount = playCountRepository.findByVideoId(videoId);
        playCount.iterateNumPlays();
        return playCountRepository.save(playCount).getNumPlays();
    }

    @Transactional(readOnly = true)
    public PlayCount readPlayCount(Video video, User user) {
        PlayCount playCount = playCountRepository.findByVideoAndUser(video, user);
        return playCount;
    }

}
