package com.convrt.controller;

import com.convrt.service.YouTubeRecommendedService;
import com.convrt.service.YouTubeSearchService;
import com.convrt.view.SearchResultWS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/youtube/recommended")
public class YouTubeRecommendedController {

    @Autowired
    private YouTubeRecommendedService youTubeRecommendedService;

    @GetMapping
    public List<SearchResultWS> getRecommended(@RequestParam("v") String videoId) {
        return youTubeRecommendedService.getRecommended(videoId);
    }

}
