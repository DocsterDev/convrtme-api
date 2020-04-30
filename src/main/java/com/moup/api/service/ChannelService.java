package com.moup.api.service;

import com.moup.api.entity.Channel;
import com.moup.api.repository.ChannelRepository;
import com.moup.api.utils.UUIDUtils;
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
        log.info("CREATING CHANNEL ID :: {}", channel.getChannelId());
        if (channelPersistent == null) {
            channel.setUuid(uuid);
            channelPersistent = channelRepository.save(channel);
        }
        if (StringUtils.isNotBlank(channel.getAvatarUrl())) {
            channelPersistent.setAvatarUrl(StringUtils.isNotBlank(channel.getAvatarUrl()) ? channel.getAvatarUrl() : channelPersistent.getAvatarUrl());
        }
        return channelPersistent;
    }

    @Transactional
    public void updateSubscribed(String channelId, String mode) {
        if (!StringUtils.equalsAny(mode, "subscribe", "unsubscribe")) {
            log.error("Channel update subscribed mode is unknown: Mode: {}", mode);
            return;
        }
        Channel channel = channelRepository.findChannelByChannelId(channelId);
        boolean isSubscribed = StringUtils.equals(mode, "subscribe");
        if (channel == null) {
            log.error("No Channel found with channel ID {} to update subscription to {}", channelId, isSubscribed);
            return;
        }
        log.info("Setting subscribed to {} for Channel ID: {}", isSubscribed, channelId);
        channel.setSubscribed(isSubscribed);
    }

    @Transactional(readOnly = true)
    public Channel readChannel(String uuid) {
        return channelRepository.getOne(uuid);
    }

    @Transactional(readOnly = true)
    public Channel readChannelByName(String name) {
        return channelRepository.findChannelByName(name);
    }
}
