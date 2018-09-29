package com.convrt.api.service;

import com.convrt.api.entity.Channel;
import com.convrt.api.repository.ChannelRepository;
import com.convrt.api.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ChannelService {
    @Autowired
    private ChannelRepository channelRepository;

    @Transactional
    public Channel addChannel(String name){
        if (name == null) {
            throw new RuntimeException("Cannot add new channel for user. Name is null.");
        }
        Channel channel = new Channel();
        channel.setUuid(UUIDUtils.generateUuid(name));
        channel.setName(name);
        return channelRepository.save(channel);
    }

    @Transactional(readOnly = true)
    public List<Channel> readAllChannels() {
        return channelRepository.findAll();
    }

}
