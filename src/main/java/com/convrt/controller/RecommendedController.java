package com.convrt.controller;

import com.convrt.entity.Video;
import com.convrt.service.RecommendedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/videos/recommended")
public class RecommendedController {

    @Autowired
    private RecommendedService recommendedService;

    @GetMapping
    public List<Video> getRecommended(@RequestParam("v") String videoId) {
        if (videoId == null) {
            throw new RuntimeException("No video ID provided for recommendation lookup");
        }
        return recommendedService.getRecommended(videoId);
    }

}
