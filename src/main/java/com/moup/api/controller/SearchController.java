package com.convrt.api.controller;

import com.convrt.api.entity.Video;
import com.convrt.api.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/videos/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public List<Video> getQuery(@RequestParam("q") String query) {
        return searchService.getSearch(query);
    }

}
