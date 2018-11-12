package com.convrt.api.service;

import com.convrt.api.entity.Channel;
import com.convrt.api.repository.ChannelRepository;
import com.convrt.api.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ChannelService {
    @Autowired
    private ChannelRepository channelRepository;

    @Transactional
    public Channel createChannel(String name) {
        Channel channel = new Channel();
        channel.setName(name);
        return createChannel(channel);
    }

    @Transactional
    public Channel createChannel(Channel channel) {
        String uuid = UUIDUtils.generateUuid(channel.getName());
        Channel channelPersistent = readChannel(uuid);
        if (channelPersistent == null) {
            channel.setUuid(uuid);
            channelPersistent = channelRepository.save(channel);
        }
        if (StringUtils.isNotBlank(channel.getAvatarUrl())) {
            channelPersistent.setAvatarUrl(StringUtils.isNotBlank(channel.getAvatarUrl()) ? channel.getAvatarUrl() : channelPersistent.getAvatarUrl());
        }
        return channelPersistent;
    }

    @Transactional(readOnly = true)
    public Channel readChannel(String uuid) {
        return channelRepository.findOne(uuid);
    }

    @Transactional(readOnly = true)
    public Channel readChannelByName(String name) {
        return channelRepository.findChannelByName(name);
    }
}
