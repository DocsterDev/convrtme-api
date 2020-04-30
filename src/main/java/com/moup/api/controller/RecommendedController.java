package com.moup.api.controller;

import com.moup.api.service.RecommendedService;
import com.moup.api.view.NowPlayingVideoWS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/videos/recommended")
public class RecommendedController {

    @Autowired
    private RecommendedService recommendedService;

    @GetMapping
    public NowPlayingVideoWS getRecommended(@RequestParam("v") String videoId) {
        if (videoId == null) {
            throw new RuntimeException("No video ID provided for recommendation lookup");
        }
        return recommendedService.getRecommended(videoId);
    }

}
