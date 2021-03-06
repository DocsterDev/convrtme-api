package com.moup.api.service;

import com.moup.api.entity.Channel;
import com.moup.api.entity.Subscription;
import com.moup.api.entity.User;
import com.moup.api.entity.UserVideo;
import com.moup.api.entity.Video;
import com.moup.api.repository.SubscriptionRepository;
import com.moup.api.repository.UserVideoRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private VideoUploadEventService videoUploadEventService;
    @Autowired
    private UserVideoRepository userVideoRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM d, uuuu").withZone(ZoneOffset.UTC);

    @Transactional
    public Subscription addSubscription(Channel channel, String token) {
        if (channel == null) {
            throw new RuntimeException("Cannot add new subscription for user. Subscription body is null.");
        }
        User user = contextService.validateUserByToken(token);
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
        String channelId = channel.getChannelId();
        // TODO - Call PubSubHub Here - Subscribe
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        log.info("Waiting until after commit");
                        videoUploadEventService.subscribe(channelId);
                    }
                });

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
        User user = contextService.validateUserByToken(token);
        Subscription subscription = subscriptionRepository.getOne(uuid);
        String channelUuid = subscription.getChannel().getUuid();
        subscriptionRepository.deleteByUuidAndUser(uuid, user);
        long subCount = subscriptionRepository.countByChannelUuid(channelUuid);
        if (subCount == 0) {
            Channel channel = channelService.readChannel(channelUuid);
            channel.setSubscribed(false);
            videoUploadEventService.unsubscribe(channel.getChannelId());
        }
    }

    @Transactional(readOnly = true)
    public List<Subscription> readSubscriptions(String token) {
        User user = contextService.validateUserByToken(token);
        return subscriptionRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Map<String, List<Video>> getSubscriptionVideos(String token, String groupBy) {
        StopWatch sw = new StopWatch();
        sw.start();
        User user = contextService.validateUserByToken(token);
        Map<String, List<Video>> subscribedVideos;
        switch (groupBy) {
            case "date":
                subscribedVideos = groupByDate(user);
                break;
            case "channel":
                subscribedVideos = groupByChannel(user);
                break;
            default:
                throw new RuntimeException(String.format("Unknown group by type: %s", groupBy));
        }
        sw.stop();
        log.info("Total time to scan for new subscribed videos {}ms", sw.getTotalTimeMillis());
        return subscribedVideos;
    }

    private Map<String, List<Video>> groupByDate(User user) {
        Map<String, List<Video>> subscribedVideos = Maps.newLinkedHashMap();
        user.getSubscriptions().stream().forEach((subscription) -> {
            subscription.getChannel().getVideos().stream().forEach((video) -> {
                List<UserVideo> watchedVideos = userVideoRepository.findDistinctByUserUuid(user.getUuid());
                //if (!isVideoWatched(video, watchedVideos)) {
                    Instant dateWatched = getDateLastWatched(video, watchedVideos);
                    if (dateWatched != null) {
                        video.setDateLastWatched(DateTimeFormatter.ISO_INSTANT.format(dateWatched));
                    }
                    String date = LocalDateTime.ofInstant(video.getSubscriptionScannedDate(), ZoneOffset.UTC).format(DATE_FORMATTER);
                    if (!subscribedVideos.containsKey(date)) {
                        subscribedVideos.put(date, Lists.newLinkedList());
                    }
                    List<Video> videos = subscribedVideos.get(date);
                  //  if (videos.size() < 3) {
                        video.setThumbnailUrl(String.format("http://i.ytimg.com/vi/%s/mqdefault.jpg", video.getId()));
                        videos.add(video);
                 //   }
                //}
            });
        });
        return subscribedVideos;
    }

    private Map<String, List<Video>> groupByChannel(User user) {
        Map<String, List<Video>> subscribedVideos = Maps.newLinkedHashMap();
        user.getSubscriptions().stream().forEach((subscription) -> {
            Channel channel = subscription.getChannel();
            String channelName = channel.getName();
            channel.getVideos().stream().forEach((video) -> {
                List<UserVideo> watchedVideos = userVideoRepository.findDistinctByUserUuid(user.getUuid());
               // if (!isVideoWatched(video, watchedVideos)) {
                    if (!subscribedVideos.containsKey(channelName)) {
                        subscribedVideos.put(channelName, Lists.newLinkedList());
                    }
                    List<Video> videos = subscribedVideos.get(channelName);
                    //if (videos.size() < 3) {
                        // String date = LocalDateTime.ofInstant(video.getSubscriptionScannedDate(), ZoneOffset.UTC).toString();
                        video.setDateScanned(DateTimeFormatter.ISO_INSTANT.format(video.getSubscriptionScannedDate()));
                        Instant dateWatched = getDateLastWatched(video, watchedVideos);
                        if (dateWatched != null) {
                            video.setDateLastWatched(DateTimeFormatter.ISO_INSTANT.format(dateWatched));
                        }
                        video.setThumbnailUrl(String.format("http://i.ytimg.com/vi/%s/mqdefault.jpg", video.getId()));
                        videos.add(video);
                   // }
               // }
            });
        });
        Map<String, List<Video>> sortedSubscribedVideos = Maps.newLinkedHashMap();
        subscribedVideos.keySet().stream().forEach((key) -> {
            List<Video> sortedPlaylist = subscribedVideos.get(key).stream().sorted((o1, o2) -> o2.getSubscriptionScannedDate().compareTo(o1.getSubscriptionScannedDate())).collect(Collectors.toList());
            sortedSubscribedVideos.put(key, sortedPlaylist);
        }); // - Heres where i can sort the keys out before building a new map
        return sortedSubscribedVideos;
    }

    private Instant getDateLastWatched(Video video, List<UserVideo> watchedVideos) {
        Optional<UserVideo> lastWatchedLog = watchedVideos.stream().filter(e -> e.getVideoId().equals(video.getId())).sorted(Comparator.comparing(UserVideo::getViewedDate).reversed()).findFirst();
        if (lastWatchedLog.isPresent()) {
            UserVideo userVideo = lastWatchedLog.get();
            return userVideo.getViewedDate();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Long pollSubscriptionVideos(String token) {
        Map<String, List<Video>> videos = getSubscriptionVideos(token, "date");
        Long count = 0L;
        for (Map.Entry<String, List<Video>> entrySet : videos.entrySet()) {
            count += entrySet.getValue().size();
        }
        return count;
    }
}
