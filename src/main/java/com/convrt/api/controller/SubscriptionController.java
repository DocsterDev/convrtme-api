package com.convrt.api.controller;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Subscription;
import com.convrt.api.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/subscription/channels")
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    public Channel addChannelSubscription(@RequestBody Channel channel, @RequestHeader("token") String token) {
        return subscriptionService.addChannelSubscription(channel, token);
    }

    @DeleteMapping("/{uuid}")
    public void deleteChannelSubscription(@PathVariable("uuid") String uuid, @RequestHeader("token") String token) {
        subscriptionService.deleteSubscription(token, uuid);
    }
}
