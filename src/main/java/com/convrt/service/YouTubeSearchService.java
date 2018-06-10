package com.convrt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class YouTubeSearchService {

    @Cacheable("query")
    public List<Object> search(String query) {
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
        return extractVideoMetadata(doc.body());
    }

    private List<Object> extractVideoMetadata(Element body) {
        Elements scripts = body.select("script").eq(8);
        String json = scripts.html().split("\r\n|\r|\n")[0];
        json = StringUtils.substring(json, 26, json.length() - 1);
        String baseQuery = "$.contents.twoColumnSearchResultsRenderer.primaryContents.sectionListRenderer.contents[0].itemSectionRenderer.contents[*].videoRenderer";

        List<Object> jsonNode = JsonPath.parse(json).read(baseQuery);
        try {
            log.info(new ObjectMapper().writeValueAsString(jsonNode));
        } catch (Exception e) {
            log.error(e.toString());
        }

        return jsonNode;
    }

}
