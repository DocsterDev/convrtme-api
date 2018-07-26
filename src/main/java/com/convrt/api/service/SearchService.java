package com.convrt.api.service;

import com.convrt.api.entity.Playlist;
import com.convrt.api.entity.Video;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SearchService {

    @Autowired
    private PlayCountService playCountService;
    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private SearchResultsService searchResultsService;

    public List<Video> getSearch(String query, String userUuid) {
        log.info("Received search request for query: {}", query);
        if (query == null) return new LinkedList<>();
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("https").host("www.youtube.com").path("/results").queryParam("q", query).build().encode();
        List<Video> results = Lists.newArrayList();
        int retryCount = 0;
        while (retryCount <= 3) {
            try {
                Stopwatch stopwatch = Stopwatch.createStarted();
                results = searchResultsService.mapSearchResultFields(uriComponents.toUriString(), userUuid);
                log.info("Took {}ms to fetch search results", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                Stopwatch stopwatch1 = Stopwatch.createStarted();
                results.stream().forEach((v) -> {
                    Video video = videoService.readVideoByVideoId(v.getId());
                    if (video != null) {
                        List<Playlist> playlists = video.getAddedByPlaylists();
                        v.getAddedByPlaylists().clear();
                        for (Playlist playlist : playlists) {
                            if (playlist.getUser().getUuid().equals(userUuid)) {
                                v.getAddedByPlaylists().add(playlist);
                            }
                        }
                    }
                });
                log.info("Took {}ms to add playlists to search results", stopwatch1.elapsed(TimeUnit.MILLISECONDS));
                log.info("Iteration");
                break;
            } catch (Exception e) {
                if (retryCount == 3) {
                    throw new RuntimeException("Error parsing json from YouTube search results after " + retryCount + " attempts", e);
                }
                log.warn("Failed parsing YouTube json. Retrying...", e);
                retryCount++;
            }
        }
        return results;
    }

}
