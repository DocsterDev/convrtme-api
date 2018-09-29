package com.convrt.api.service;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Context;
import com.convrt.api.entity.User;
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
    @Autowired
    private ContextService contextService;

    @Transactional
    public Channel addSubscription(String name, String token){
        if (name == null || token == null) {
            throw new RuntimeException("Cannot add new subscription for user. Channel name or token is null.");
        }
        Context context = contextService.validateContext(token);
        User user = context.getUser();
        if (user == null) {
            throw new RuntimeException("Cannot find user to add channel subscription.");
        }
        Channel channel = new Channel(name);
        channel.getSubscribers().add(user);
        return channelRepository.save(channel);
    }

    @Transactional(readOnly = true)
    public List<Channel> readAllChannels() {
        return channelRepository.findAll();
    }

}
