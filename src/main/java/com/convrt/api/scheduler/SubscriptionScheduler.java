package com.convrt.api.scheduler;

import com.convrt.api.entity.Video;
import com.convrt.api.service.ChannelService;
import com.convrt.api.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SubscriptionScheduler {
    @Autowired
    private SearchService searchService;
    @Autowired
    private ChannelService channelService;

    @Scheduled(fixedRate = 360000)
    @Transactional
    public void testScheduler() {
        log.info("Test here");
        //searchService.getSearch();
        channelService.readAllChannels().stream().forEach((channel) -> {
            if (channel.getSubscribers().isEmpty()) {
                List<Video> channelVideoResults = searchService.getSearch(String.format("%s new videos", channel.getName()));
                channelVideoResults.stream().forEach((video) -> {
                    if (video.getOwner().equals(channel.getName()) && video.isNew()) {
                        log.info("WOOOO New video for {} found! {}", channel.getName(), video.getTitle());
                    }
                });
            }
        });
    }

}
