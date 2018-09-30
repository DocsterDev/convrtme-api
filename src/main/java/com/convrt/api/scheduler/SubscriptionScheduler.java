package com.convrt.api.scheduler;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Subscription;
import com.convrt.api.entity.Video;
import com.convrt.api.service.SubscriptionService;
import com.convrt.api.service.SearchService;
import com.convrt.api.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class SubscriptionScheduler {
    @Autowired
    private SearchService searchService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private VideoService videoService;

    @Scheduled(fixedRate = 360000)
    @Transactional
    public void scanNewVideos() {
        subscriptionService.readAllDistinctChannels().stream().forEach((channel) -> {
            log.info("Running search : {}", String.format("%s new videos", channel.getName()));
            List<Video> channelVideoResults = searchService.getSearch(String.format("%s new videos", channel.getName()));
            channelVideoResults.stream().forEach((video) -> {
                //log.info("Compare {} vs {} =? {}", video.getOwner(), channel.getName(), StringUtils.equalsAny(video.getOwner(), channel.getName()));
                if (video.getOwner().equals(channel.getName()) && video.isNew()) {
                    updateOrAddVideo(video, channel);
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
        });
    }


    @Transactional
    public void updateOrAddVideo(Video video, Channel channel){
        Video videoPersistent = videoService.readVideoByVideoId(video.getId());
        if (videoPersistent == null) {
            videoPersistent = new Video();
            videoPersistent.setId(video.getId());
            log.info("Adding new video to lineup: {} by {}", video.getTitle(), video.getOwner());
        }
        if (videoPersistent != null && videoPersistent.getSubscriptionScannedDate() != null) {
            log.info("Video already added for subscription notification - {}", channel.getName());
            return;
        }
        log.info("Update video for subscription notification - {}", channel.getName());
        videoPersistent.setTitle(video.getTitle());
        videoPersistent.setDuration(video.getDuration());
        videoPersistent.setViewCount(video.getViewCount());
        videoPersistent.setChannel(channel);
        videoPersistent.setSubscriptionScannedDate(Instant.now());
        videoService.createOrUpdateVideo(videoPersistent);
    }

}
