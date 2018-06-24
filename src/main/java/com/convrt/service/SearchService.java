package com.convrt.service;

import com.convrt.view.SearchResultWS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class SearchService {

    @Autowired
    private PlayCountService playCountService;

    private static final ObjectMapper MAPPER = new ObjectMapper();


    @Cacheable("search")
    public List<SearchResultWS> getSearch(String query) {
        log.info("Received search request for query: {}", query);
        if (query == null) return new LinkedList<>();
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("https").host("www.youtube.com").path("/results").queryParam("q", query).build().encode();
        List<SearchResultWS> results = Lists.newArrayList();
        int retryCount = 0;
        while (retryCount <= 3) {
            try {
                Document doc = Jsoup.connect(uriComponents.toUriString()).get();
                results = mapSearchResultFields(doc.body());
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

    private JsonNode parseSearchResults(Element body) throws IOException {
        Elements scripts = body.select("script");
        String script = null;
        for (int i = 0; i < scripts.size(); i++) {
            String html = scripts.eq(i).html();
            if ( html.contains("window[\"ytInitialData\"]")) {
                log.info("Found Search at: " + i);
                script = html;
                break;
            }
        }
        String json = script.split("\r\n|\r|\n")[0];
        json = StringUtils.substring(json, 26, json.length() - 1);
        JsonNode jsonNode = MAPPER.readTree(json);
        return jsonNode.get("contents").get("twoColumnSearchResultsRenderer").get("primaryContents").get("sectionListRenderer").get("contents").get(0).get("itemSectionRenderer").get("contents");
    }

    private List<SearchResultWS> mapSearchResultFields(Element body) throws IOException {
        List<SearchResultWS> searchResults = Lists.newArrayList();
        Iterator<JsonNode> iterator = parseSearchResults(body).iterator();
        while (iterator.hasNext()) {
            try {
                SearchResultWS searchResult = new SearchResultWS();
                JsonNode next = iterator.next().get("videoRenderer");
                searchResult.setVideoId(next.get("videoId").asText());
                int thumbnailSize = next.get("thumbnail").get("thumbnails").size();
                searchResult.setThumbnailUrl(next.get("thumbnail").get("thumbnails").get(thumbnailSize-1).get("url").asText());
                searchResult.setTitle(next.get("title").get("simpleText").asText());
                searchResult.setOwner(next.get("shortBylineText").get("runs").get(0).get("text").asText());
                searchResult.setViewCount(next.get("shortViewCountText").get("simpleText").asText());
                searchResult.setDuration(next.get("thumbnailOverlays").get(0).get("thumbnailOverlayTimeStatusRenderer").get("text").get("simpleText").asText());
                searchResult.setPublishedTimeAgo(next.get("publishedTimeText").get("simpleText").asText());
                // searchResult.setPlayCount(playCountService.readNumPlaysByVideoId(searchResult.getVideoId()));
                searchResults.add(searchResult);
            } catch (NullPointerException e) {
                log.error("Search result is null. Not including in results.");
            }
        }
        return searchResults;
    }
}
