package com.convrt.service;

import com.convrt.entity.Context;
import com.convrt.entity.PlayCount;
import com.convrt.entity.User;
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
    public Long iterateNumPlays(Context context, String videoId) {
        PlayCount playCount = playCountRepository.findByUserUuidAndVideoId(context.getUuid(), videoId);
        if (playCount == null) {
            playCount = new PlayCount();
            playCount.setUuid(UUID.randomUUID().toString());
            playCount.setVideoId(videoId);
            playCount.setContext(context);
        }
        playCount.iterateNumPlays();
        return playCountRepository.save(playCount).getNumPlays();
    }

}
