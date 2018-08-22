package com.convrt.api.controller;

import com.convrt.api.entity.Video;
import com.convrt.api.service.RecommendedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/videos/recommended")
public class RecommendedController {

    @Autowired
    private RecommendedService recommendedService;

    @GetMapping
    public List<Video> getRecommended(@RequestHeader("User-Agent") String userAgent, @RequestParam("v") String videoId) {
        if (videoId == null) {
            throw new RuntimeException("No video ID provided for recommendation lookup");
        }
        return recommendedService.getRecommended(videoId);
    }

}
