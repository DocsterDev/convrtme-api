package com.convrt.api.controller;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Context;
import com.convrt.api.service.ChannelService;
import com.convrt.api.service.ContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/channels")
public class SubscriptionController {
    @Autowired
    private ChannelService channelService;

    @PostMapping("/{name}/subscribe")
    public void addSubscription(@PathVariable("name") String name, @RequestHeader("token") String token){
        channelService.addSubscription(name, token);
    }
}
