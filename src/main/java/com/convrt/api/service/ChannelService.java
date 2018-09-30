package com.convrt.api.service;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Subscription;
import com.convrt.api.entity.Video;
import com.convrt.api.repository.ChannelRepository;
import com.convrt.api.repository.VideoRepository;
import com.convrt.api.utils.UUIDUtils;
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

@Slf4j
@Service
public class ChannelService {
    @Autowired
    private ChannelRepository channelRepository;

    @Transactional
    public Channel createChannel(Channel channel) {
        String uuid = UUIDUtils.generateUuid(channel.getName());
        Channel channelPersistent = readChannel(uuid);
        if (channelPersistent == null) {
            channel.setUuid(uuid);
            channelPersistent = channelRepository.save(channel);
        }
        return channelPersistent;
    }

    @Transactional(readOnly = true)
    public Channel readChannel(String uuid) {
        return channelRepository.findOne(uuid);
    }
}
