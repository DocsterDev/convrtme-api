package com.convrt.service;

import com.convrt.entity.PlayCount;
import com.convrt.entity.User;
import com.convrt.entity.Video;
import com.convrt.repository.PlayCountRepository;
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
