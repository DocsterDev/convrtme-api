package com.convrt.api.service;

import com.convrt.api.entity.*;
import com.convrt.api.repository.SubscriptionRepository;
import com.convrt.api.view.Status;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private ContextService contextService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private VideoService videoService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd").withZone(ZoneOffset.UTC);

    @Transactional
    public Subscription addSubscription(Channel channel, String token) {
        if (channel == null) {
            throw new RuntimeException("Cannot add new subscription for user. Subscription body is null.");
        }
        User user = contextService.validateAndGetUser(token);
        channel = channelService.createChannel(channel);
        Subscription sub = new Subscription();
        sub.setUuid(UUID.randomUUID().toString());
        sub.setUser(user);
        sub.setChannel(channel);
        sub.setSubscribedDate(Instant.now());
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
        User user = contextService.validateAndGetUser(token);
        subscriptionRepository.deleteByUuidAndUser(uuid, user);
    }

    @Transactional(readOnly = true)
    public List<Subscription> readSubscriptions(String token) {
        User user = contextService.validateAndGetUser(token);
        return subscriptionRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Map<String, List<Video>> getSubscriptionVideos(String token) {
        StopWatch sw = new StopWatch();
        sw.start();
        User user = contextService.validateAndGetUser(token);
        List<Video> watchedVideos = user.getVideos();
        Map<String, List<Video>> newVideos = Maps.newLinkedHashMap();
        for (Subscription subscription : subscriptionRepository.findByUser(user)) {
            for (Video video : videoService.findVideosByChannel(subscription.getChannel(), subscription.getSubscribedDate())) {
                if (!isVideoWatched(video, watchedVideos)) {
                    String date = LocalDateTime.ofInstant(video.getSubscriptionScannedDate(), ZoneOffset.UTC).format(DATE_FORMATTER);
                    if (!newVideos.containsKey(date)) {
                        newVideos.put(date, Lists.newLinkedList());
                    }
                    newVideos.get(date).add(video);
                }
            }
        }
        sw.stop();
        log.info("Total time to scan for new subscribed videos {}ms", sw.getTotalTimeMillis());
        return newVideos;
    }

    private boolean isVideoWatched(Video video, List<Video> watchedVideos) {
        for (Video watchedVideo : watchedVideos) {
            if (video.getId().equals(watchedVideo.getId())) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Status pollSubscriptionVideos(String token) {
        StopWatch sw = new StopWatch();
        sw.start();
        User user = contextService.validateAndGetUser(token);
        List<Video> watchedVideos = user.getVideos();
        for (Subscription subscription : subscriptionRepository.findByUser(user)) {
            for (Video video : videoService.findVideosByChannel(subscription.getChannel(), subscription.getSubscribedDate())) {
                for (Video watchedVideo : watchedVideos) {
                    if (video.getId().equals(watchedVideo.getId())) {
                        return new Status(true);
                    }
                }
            }
        }
        sw.stop();
        log.info("Total time to scan for new subscribed videos {}ms", sw.getTotalTimeMillis());
//        subscriptionRepository.findByUser(user).forEach((subscription) -> {
//            videoService.findVideosByChannel(subscription.getChannel(), subscription.getSubscribedDate()).forEach((video) -> {
//                watchedVideos.forEach((watchedVideo) -> {
//                    if (video.getId().equals(watchedVideo.getId())) {
//                        return;
//                    }
//                });
//            });
//        });
        return new Status(false);
    }

}
