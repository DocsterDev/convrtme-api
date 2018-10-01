package com.convrt.api.service;

import com.convrt.api.entity.Video;
import com.convrt.api.utils.MappingUtils;
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
public class RecommendedService {

    @Autowired
    private ObjectMapper objectMapper;

    @Cacheable("recommended")
    public List<Video> getRecommended(String videoId) {
        log.info("Received recommended request for video: {}", videoId);
        if (StringUtils.isBlank(videoId)) return new LinkedList<>();
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("https").host("www.youtube.com").path("/watch").queryParam("v", videoId).build().encode();
        List<Video> results = Lists.newArrayList();
        int retryCount = 0;
        while (retryCount <= 2) {
            try {
                Document doc = Jsoup.connect(uriComponents.toUriString()).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36").get();
                results = mapRecommendedFields(doc.body());
                break;
            } catch (Exception e) {
                if (retryCount == 2) {
                    throw new RuntimeException("Error parsing json from YouTube recommended results after " + retryCount + 1 + " attempts", e);
                }
                log.warn("Failed parsing YouTube json. Retrying...", e);
                try { Thread.sleep(1000); } catch (Exception ex) { }
                retryCount++;
            }
        }
        return results;
    }

    private List<Video> mapRecommendedFields(Element body) throws IOException {
        List<Video> searchResults = Lists.newArrayList();
        Iterator<JsonNode> iterator = parseRecommendedResults(body).iterator();
        int i = 0;
        while (iterator.hasNext()) {
            try {
                JsonNode next;
                if (i == 0) {
                    next = iterator.next().get("compactAutoplayRenderer").get("contents").get(0).get("compactVideoRenderer");
                } else {
                    next = iterator.next().get("compactVideoRenderer");
                }
                Video searchResult = new Video();
                searchResult.setId(next.get("videoId").asText());
                searchResult.setThumbnailUrl(String.format("http://i.ytimg.com/vi/%s/mqdefault.jpg", searchResult.getId()));
                searchResult.setTitle(next.get("title").get("simpleText").asText());
                searchResult.setOwner(next.get("shortBylineText").get("runs").get(0).get("text").asText());
                searchResult.setViewCount(next.get("shortViewCountText").get("simpleText").asText());
                searchResult.setDuration(next.get("lengthText").get("simpleText").asText());
                JsonNode badges = next.get("badges");
                MappingUtils.findIsNew(next, searchResult, badges);
                searchResults.add(searchResult);
            } catch (NullPointerException e) {
                log.error("Search result is null. Not including in results.");
            }
            i++;
        }
        return searchResults;
    }

    private JsonNode parseRecommendedResults(Element body) throws IOException {
        Elements scripts = body.select("script");
        String script = null;
        for (int i = 0; i < scripts.size(); i++) {
            String html = scripts.eq(i).html();
            if ( html.contains("window[\"ytInitialData\"]")) {
                log.info("Found Recommended at: " + i);
                script = html;
               break;
            }
        }
        String json = script.split("\r\n|\r|\n")[0];
        json = StringUtils.substring(json, 26, json.length() - 1);
        JsonNode jsonNode = objectMapper.readTree(json);
        return jsonNode.get("contents").get("twoColumnWatchNextResults").get("secondaryResults").get("secondaryResults").get("results");
    }
}
