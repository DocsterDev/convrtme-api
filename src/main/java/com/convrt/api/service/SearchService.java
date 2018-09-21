package com.convrt.api.service;

import com.convrt.api.entity.Video;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class SearchService {
    @Autowired
    private SearchResultsService searchResultsService;
    @Autowired
    private VideoService videoService;

    public List<Video> getSearch(String query) {
        log.info("Received search request for query: {}", query);
        if (query == null) return Lists.newLinkedList();
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("https").host("www.youtube.com").path("/results").queryParam("q", query).build().encode();
        List<Video> results = Lists.newArrayList();
        int retryCount = 0;
        while (retryCount <= 3) {
            try {
                results = searchResultsService.mapSearchResultFields(uriComponents.toUriString());
                break;
            } catch (Exception e) {
                if (retryCount == 3) {
                    throw new RuntimeException("Error parsing json from YouTube search results after " + retryCount + " attempts", e);
                }
                log.warn("Failed parsing YouTube json. Retrying...", e);
                try { Thread.sleep(100); } catch (Exception ex) { }
                retryCount++;
            }
        }
        return results;
    }

}
