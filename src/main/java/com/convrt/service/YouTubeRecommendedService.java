package com.convrt.service;

import com.convrt.view.SearchResultWS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class YouTubeRecommendedService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Cacheable("recommended")
    public JsonNode getRecommended(String videoId) {
        log.info("Received recommended request for video: {}", videoId);
        if (videoId == null)
            return null;
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("https").host("www.youtube.com").path("/watch").queryParam("v", "soTXxzKWhG0").build().encode();
        Document doc;
        try {
            doc = Jsoup.connect(uriComponents.toUriString()).get();

        } catch (Exception e) {
            throw new RuntimeException("Error parsing document", e);
        }
        return parseRecommendedResults(doc);
    }


    static int i = 0;

    private JsonNode parseRecommendedResults(Element body) {
        JsonNode jsonNode = null;
        try {
            Elements scripts = body.select("script").eq(27);
            String json = scripts.html().split("\r\n|\r|\n")[0];
            json = StringUtils.substring(json, 26, json.length() - 1);
            jsonNode = MAPPER.readTree(json);
            JsonNode objNode = jsonNode.get("contents").get("twoColumnWatchNextResults").get("secondaryResults").get("secondaryResults").get("results");
            Iterator<JsonNode> iterator = objNode.iterator();
            i = 0;
            iterator.forEachRemaining((e) -> {
                if (i == 0) {
                    log.info("Up Next: " + e.get("compactAutoplayRenderer").get("contents").get(0).get("compactVideoRenderer"));
                } else {
                    log.info("Entry: " + e.get("compactVideoRenderer"));
                }
                i++;
            });
        } catch (Exception e) {
            log.warn("Failed parsing YouTube json. Retrying...");
        }
        return jsonNode;
    }


}
