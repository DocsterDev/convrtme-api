package com.convrt.controller;

import com.convrt.service.YouTubeSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/youtube/search")
public class YouTubeSearchController {

    @Autowired
    private YouTubeSearchService youTubeSearchService;

    @GetMapping
    public List<Object> getQuery(@RequestParam("q") String query) {
        return youTubeSearchService.search(query);
    }

}
