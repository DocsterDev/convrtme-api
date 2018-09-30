package com.convrt.api.service;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Context;
import com.convrt.api.entity.Subscription;
import com.convrt.api.entity.User;
import com.convrt.api.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public Subscription addSubscription(Channel channel, String token) {
        if (channel == null) {
            throw new RuntimeException("Cannot add new subscription for user. Subscription body is null.");
        }
        Context context = contextService.validateContext(token);
        User user = context.getUser();
        if (user == null) {
            throw new RuntimeException("Cannot find user to add subscription subscription.");
        }
        channel = channelService.createChannel(channel);
        Subscription sub = new Subscription();
        sub.setUuid(UUID.randomUUID().toString());
        sub.setUser(user);
        sub.setChannel(channel);
        if (subscriptionRepository.existsByChannelAndUser(channel, user)) {
            throw new RuntimeException(String.format("You have already subscribed to %s", channel.getName()));
        }
        subscriptionRepository.save(sub);
        return sub;
    }

    @Transactional(readOnly = true)
    public List<Channel> readAllDistinctChannels() {
        return subscriptionRepository.findDistinctChannel();
    }

    @Transactional
    public void deleteSubscription(String token, String uuid) {
        if (uuid == null) {
            throw new RuntimeException("Cannot delete subscription for user. Subscription uuid is null.");
        }
        Context context = contextService.validateContext(token);
        User user = context.getUser();
        if (user == null) {
            throw new RuntimeException("Cannot find user to delete channel subscription.");
        }
        subscriptionRepository.deleteByUuidAndUser(uuid, user);
    }

    @Transactional(readOnly = true)
    public List<Subscription> readSubscriptions(String token) {
        Context context = contextService.validateContext(token);
        User user = context.getUser();
        if (user == null) {
            throw new RuntimeException("Cannot find user to fetch channel subscriptions.");
        }
        return subscriptionRepository.findByUser(user);
    }
}
