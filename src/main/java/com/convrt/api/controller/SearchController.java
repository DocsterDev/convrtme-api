package com.convrt.api.controller;

import com.convrt.api.entity.Context;
import com.convrt.api.entity.Video;
import com.convrt.api.service.ContextService;
import com.convrt.api.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/videos/search")
public class SearchController {

    @Autowired
    private SearchService searchService;
    @Autowired
    private ContextService contextService;

    @GetMapping
    public List<Video> getQuery(@RequestHeader("User-Agent") String userAgent, @RequestHeader(value = "token") String token, @RequestParam("q") String query) {
        String userUuid = null;
        if (token != null) {
            Context context = contextService.validateContext(token);
            userUuid = context.getUser().getUuid();
        }
        return searchService.getSearch(query, userUuid);
    }

}
