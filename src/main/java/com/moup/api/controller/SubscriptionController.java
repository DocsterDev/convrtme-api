package com.moup.api.controller;

import com.moup.api.entity.Channel;
import com.moup.api.entity.Subscription;
import com.moup.api.entity.Video;
import com.moup.api.service.SubscriptionService;
import com.moup.api.view.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    public Subscription addSubscription(@RequestBody Channel channel, @RequestHeader("token") String token) {
        return subscriptionService.addSubscription(channel, token);
    }

    @DeleteMapping("/{uuid}")
    public void deleteSubscription(@PathVariable("uuid") String uuid, @RequestHeader("token") String token) {
        subscriptionService.deleteSubscription(token, uuid);
    }

    @GetMapping
    public List<Subscription> readSubscriptions(@RequestHeader("token") String token) {
        return subscriptionService.readSubscriptions(token);
    }

    @GetMapping("/videos")
    public Map<String, List<Video>> getSubscriptionVideos(@RequestHeader("token") String token, @RequestParam("groupBy") String groupBy) {
        return subscriptionService.getSubscriptionVideos(token, groupBy);
    }

    @GetMapping("/poll")
    public Status pollSubscriptionVideos(@RequestHeader("token") String token) {
         return new Status(subscriptionService.pollSubscriptionVideos(token));
    }
}
