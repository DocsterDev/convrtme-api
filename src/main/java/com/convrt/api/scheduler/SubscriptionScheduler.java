package com.convrt.api.scheduler;

import com.convrt.api.entity.Subscription;
import com.convrt.api.entity.Video;
import com.convrt.api.service.SubscriptionService;
import com.convrt.api.service.SearchService;
import com.convrt.api.service.VideoService;
import lombok.extern.slf4j.Slf4j;
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
    public void testScheduler() {
        log.info("Test here");
//        subscriptionService.readAllDistinctChannels().stream().forEach((channel) -> {
//            List<Video> channelVideoResults = searchService.getSearch(String.format("%s new videos", channel));
//            channelVideoResults.stream().forEach((video) -> {
//                if (video.getChannel().equals(channel) && video.isNew()) {
//                    log.info("WOOOO New video for {} found! {}", channel, video.getTitle());
//
//                    // CHECK IS THIS VIDEO ALREADY EXISTS IN THE DB IF IT DOES, THEN FETCH IT AND APPEND IT WITH TITLE, CHANNEL, ETC and flag it as a subscription-found video
//                    updateOrAddVideo(video);
//                }
//            });
//        });
    }

    private void updateOrAddVideo(Video video){
        Video videoPersistent = videoService.readVideo(video.getId());
        if (videoPersistent == null) {
            videoPersistent = video;
        }
        videoPersistent.setSubscriptionScannedDate(Instant.now());
    }

}
