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
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class YouTubeSearchService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    int retryCount = 0;

    @Cacheable("query")
    public List<SearchResultWS> search(String query) {
        log.info("Received search request for query: {}", query);
        if (query == null)
            return new LinkedList<>();
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.youtube.com")
                .path("/results")
                .queryParam("q", query)
                .build()
                .encode();
        String uri = uriComponents.toUriString();
        Document doc;
        try {
            doc = Jsoup.connect(uri).get();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing document", e);
        }
        return mapYouTubeJsonFields(doc.body());
    }

    private List<SearchResultWS> mapYouTubeJsonFields(Element body) {
    JsonNode jsonNode = parseYouTubeJson(body);
        List<SearchResultWS> searchResults = Lists.newArrayList();
        Iterator<JsonNode> iterator = jsonNode.iterator();
        while (iterator.hasNext()) {
            try {
                SearchResultWS searchResult = new SearchResultWS();
                JsonNode next = iterator.next();
                searchResult.setVideoId(next.path("videoId").asText());
                searchResult.setTitle(next.get("title").get("simpleText").asText());
                searchResult.setThumbnailUrl(next.get("thumbnail").get("thumbnails").get(0).get("url").asText());
                searchResult.setOwner(next.get("shortBylineText").get("runs").get(0).get("text").asText());
                searchResult.setViewCount(next.get("shortViewCountText").get("simpleText").asText());
//              searchResult.setDuration(next.get("thumbnailOverlays").get(0).get("thumbnailOverlayTimeStatusRenderer").get("text").get("simpleText").asText());
//              searchResult.setPublishedTimeAgo(next.get("publishedTimeText").get("simpleText").asText());
                searchResults.add(searchResult);
            } catch (NullPointerException e) {
                throw new RuntimeException("Error mapping json fields from YouTube search results", e);
            }
        }
        return searchResults;
    }

    private JsonNode parseYouTubeJson(Element body) {
        JsonNode jsonNode = null;
        retryCount = 0;
        while (retryCount <= 3) {
            try {
                Elements scripts = body.select("script").eq(8);
                String json = scripts.html().split("\r\n|\r|\n")[0];
                json = StringUtils.substring(json, 26, json.length() - 1);
                String baseQuery = "$.contents.twoColumnSearchResultsRenderer.primaryContents.sectionListRenderer.contents[0].itemSectionRenderer.contents[*].videoRenderer";
                String jsonStr = JsonPath.parse(json).read(baseQuery).toString();
                jsonNode = MAPPER.readTree(jsonStr);
                break;
            } catch (NullPointerException e) {
                log.error("Bro, we hit an error");
                if (retryCount > 3) {
                    throw new RuntimeException("Error parsing json from YouTube search results", e);
                }
                log.warn("Failed parsing YouTube json. Retrying...");
                retryCount++;
            } catch (Exception e) {
                if (retryCount > 3) {
                    throw new RuntimeException("Error parsing json from YouTube search results", e);
                }
                log.warn("Failed parsing YouTube json. Retrying...");
                retryCount++;
            }
        }
        return jsonNode;
    }

}
