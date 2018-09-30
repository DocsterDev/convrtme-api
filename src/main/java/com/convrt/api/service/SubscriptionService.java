package com.convrt.api.service;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Subscription;
import com.convrt.api.entity.Context;
import com.convrt.api.entity.User;
import com.convrt.api.repository.ChannelRepository;
import com.convrt.api.repository.SubscriptionRepository;
import com.convrt.api.utils.UUIDUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private ContextService contextService;
    @Autowired
    private ChannelService channelService;

    @Transactional
    public Channel addChannelSubscription(Channel channel, String token) {
        if (channel == null || token == null) {
            throw new RuntimeException("Cannot add new subscription for user. Subscription name or token is null.");
        }
        Context context = contextService.validateContext(token);
        User user = context.getUser();
        if (user == null) {
            throw new RuntimeException("Cannot find user to add subscription subscription.");
        }
        channel = channelService.createChannel(channel);
        String compositeKey = UUIDUtils.generateUuid(String.format("%s-%s", channel.getUuid(), user.getUuid()));
        Map<String, Subscription> subscriptions = channel.getSubscriptions();
        if (subscriptions != null && subscriptions.containsKey(compositeKey)) {
            throw new RuntimeException(String.format("You are already subscribed to %s", channel.getName()));
        }
        Subscription sub = new Subscription();
        sub.setUuid(UUID.randomUUID().toString());
        sub.setUser(user);
        sub.setChannel(channel);
        channel.getSubscriptions().put(compositeKey, sub);
        return channel;
    }

    @Transactional(readOnly = true)
    public List<Subscription> readAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<String> readAllDistinctChannels() {
        return subscriptionRepository.findDistinctChannel();
    }

    @Transactional
    public void deleteSubscription(String token, String uuid) {
        Context context = contextService.validateContext(token);
        User user = context.getUser();
        if (user == null) {
            throw new RuntimeException("Cannot find user to delete channel subscription.");
        }
        Channel channel = channelService.readChannel(uuid);
        String compositeKey = UUIDUtils.generateUuid(String.format("%s-%s", uuid, user.getUuid()));
        log.info("Composite key: {}", compositeKey);
        if (!channel.getSubscriptions().containsKey(compositeKey)) {
            throw new RuntimeException("Channel not found for user to delete");
        }
        channel.getSubscriptions().remove(compositeKey);
    }
}
