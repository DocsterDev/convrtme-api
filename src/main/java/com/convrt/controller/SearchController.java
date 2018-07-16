package com.convrt.controller;

import com.convrt.entity.Context;
import com.convrt.entity.Video;
import com.convrt.service.ContextService;
import com.convrt.service.SearchService;
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
    public List<Video> getQuery(@RequestHeader(value = "token") String token, @RequestParam("q") String query) {
        String userUuid = null;
        if (token != null) {
            Context context = contextService.validateContext(token);
            userUuid = context.getUser().getUuid();
        }
        return searchService.getSearch(query, userUuid);
    }

}
