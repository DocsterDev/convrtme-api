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
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("https").host("www.youtube.com").path("/results").queryParam("q", query).build().encode();
        Document doc;
        List<SearchResultWS> results = Lists.newArrayList();
        retryCount = 0;
        while (retryCount <= 3) {
            try {
                doc = Jsoup.connect(uriComponents.toUriString()).get();
                results = mapYouTubeJsonFields(doc.body());
                break;
            } catch (Exception e) {
                if (retryCount > 0) {
                    throw new RuntimeException("Error parsing json from YouTube search results", e);
                }
                retryCount++;
                log.warn("Failed parsing YouTube json. Retrying...", e);
            }
        }
        return results;
    }

    private JsonNode parseSearchResults(Element body) throws IOException {
        JsonNode jsonNode = null;
        Elements scripts = body.select("script").eq(8);
        String json = scripts.html().split("\r\n|\r|\n")[0];
        json = StringUtils.substring(json, 26, json.length() - 1);
        log.info(json);
        jsonNode = MAPPER.readTree(json);
        JsonNode objNode = jsonNode.get("contents").get("twoColumnSearchResultsRenderer").get("primaryContents").get("sectionListRenderer").get("contents").get(0).get("itemSectionRenderer").get("contents");
        log.info(objNode.asText());
        //String baseQuery = "$.contents.twoColumnSearchResultsRenderer.primaryContents.sectionListRenderer.contents[0].itemSectionRenderer.contents[*].videoRenderer";
        //String jsonStr = JsonPath.parse(json).read(baseQuery).toString();
        //jsonNode = MAPPER.readTree(jsonStr);
        return jsonNode;
    }

    private List<SearchResultWS> mapYouTubeJsonFields(Element body) throws IOException {
        JsonNode jsonNode = parseSearchResults(body);
        List<SearchResultWS> searchResults = Lists.newArrayList();
        Iterator<JsonNode> iterator = jsonNode.iterator();
        while (iterator.hasNext()) {
            try {
                SearchResultWS searchResult = new SearchResultWS();
                JsonNode next = iterator.next().get("videoRenderer");
                searchResult.setVideoId(next.path("videoId").asText());
                searchResult.setTitle(next.get("title").get("simpleText").asText());
                int thumbnailSize = next.get("thumbnail").get("thumbnails").size();
                searchResult.setThumbnailUrl(next.get("thumbnail").get("thumbnails").get(thumbnailSize-1).get("url").asText());
                searchResult.setOwner(next.get("shortBylineText").get("runs").get(0).get("text").asText());
                searchResult.setViewCount(next.get("shortViewCountText").get("simpleText").asText());
                searchResult.setDuration(next.get("thumbnailOverlays").get(0).get("thumbnailOverlayTimeStatusRenderer").get("text").get("simpleText").asText());
                searchResult.setPublishedTimeAgo(next.get("publishedTimeText").get("simpleText").asText());
                searchResults.add(searchResult);
            } catch (NullPointerException e) {
                throw new RuntimeException("Error mapping json fields from YouTube search results", e);
            }
        }
        return searchResults;
    }





}
