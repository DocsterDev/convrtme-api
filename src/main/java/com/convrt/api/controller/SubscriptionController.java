package com.convrt.api.controller;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Subscription;
import com.convrt.api.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
