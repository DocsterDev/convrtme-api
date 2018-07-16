package com.convrt.service;

import com.convrt.entity.Video;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class SearchResultsService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Cacheable("search")
    public List<Video> mapSearchResultFields(String url, String userUuid) throws IOException {
        Document doc = Jsoup.connect(url).get();
        List<Video> searchResults = Lists.newArrayList();
        Iterator<JsonNode> iterator = parseSearchResults(doc.body()).iterator();
        while (iterator.hasNext()) {
            try {
                Video searchVideo = new Video();
                JsonNode next = iterator.next().get("videoRenderer");
                searchVideo.setId(next.get("videoId").asText());
                int thumbnailSize = next.get("thumbnail").get("thumbnails").size();
                searchVideo.setThumbnailUrl(next.get("thumbnail").get("thumbnails").get(thumbnailSize-1).get("url").asText());
                searchVideo.setTitle(next.get("title").get("simpleText").asText());
                searchVideo.setOwner(next.get("shortBylineText").get("runs").get(0).get("text").asText());
                searchVideo.setViewCount(next.get("shortViewCountText").get("simpleText").asText());
                searchVideo.setDuration(next.get("thumbnailOverlays").get(0).get("thumbnailOverlayTimeStatusRenderer").get("text").get("simpleText").asText());
                searchVideo.setPublishedTimeAgo(next.get("publishedTimeText").get("simpleText").asText());
                searchResults.add(searchVideo);
            } catch (NullPointerException e) {
                log.error("Search result is null. Not including in results.");
            }
        }
        return searchResults;
    }

    private JsonNode parseSearchResults(Element body) throws IOException {
        Elements scripts = body.select("script");
        String script = null;
        for (int i = 0; i < scripts.size(); i++) {
            String html = scripts.eq(i).html();
            if ( html.contains("window[\"ytInitialData\"]")) {
                script = html;
                break;
            }
        }
        String json = script.split("\r\n|\r|\n")[0];
        json = StringUtils.substring(json, 26, json.length() - 1);
        JsonNode jsonNode = MAPPER.readTree(json);
        return jsonNode.get("contents").get("twoColumnSearchResultsRenderer").get("primaryContents").get("sectionListRenderer").get("contents").get(0).get("itemSectionRenderer").get("contents");
    }

}
