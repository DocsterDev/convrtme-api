package com.moup.api.service;

import com.moup.api.entity.Video;
import com.moup.api.utils.MappingUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class SearchResultsService {

    @Autowired
    private ObjectMapper objectMapper;

    //@Cacheable("search")
    @Transactional(readOnly = true)
    public List<Video> mapSearchResultFields(String url) throws IOException {
        Connection connection = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        Document doc = connection.get();
        List<Video> searchResults = Lists.newArrayList();
        Iterator<JsonNode> iterator = parseSearchResults(doc.body()).iterator();
        while (iterator.hasNext()) {
            try {
                Video searchVideo = new Video();
                JsonNode next = iterator.next().get("videoRenderer");
                searchVideo.setId(next.get("videoId").asText());
                searchVideo.setThumbnailUrl(String.format("http://i.ytimg.com/vi/%s/mqdefault.jpg", searchVideo.getId()));
                searchVideo.setTitle(next.get("title").get("simpleText").asText());
                JsonNode owner = next.get("shortBylineText").get("runs").get(0);
                String channelId = owner.get("navigationEndpoint").get("browseEndpoint").get("browseId").asText();
                searchVideo.setChannelId(channelId);
                searchVideo.setOwner(owner.get("text").asText());
                searchVideo.setViewCount(next.get("shortViewCountText").get("simpleText").asText());
                searchVideo.setDuration(next.get("thumbnailOverlays").get(0).get("thumbnailOverlayTimeStatusRenderer").get("text").get("simpleText").asText());
                searchVideo.setPublishedTimeAgo(next.get("publishedTimeText").get("simpleText").asText());
                if (next.hasNonNull("channelThumbnail")) {
                    searchVideo.setChannelThumbnailUrl(next.get("channelThumbnail").get("thumbnails").get(0).get("url").asText());
                } else if (next.hasNonNull("channelThumbnailSupportedRenderers")) {
                    searchVideo.setChannelThumbnailUrl(next.get("channelThumbnailSupportedRenderers").get("channelThumbnailWithLinkRenderer").get("thumbnail").get("thumbnails").get(0).get("url").asText());
                }
                JsonNode badges = next.get("badges");
                MappingUtils.findIsNew(next, searchVideo, badges);
                searchResults.add(searchVideo);
            } catch (NullPointerException e) {
                log.info("Search result is null. Not including in results.");
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
        JsonNode jsonNode = objectMapper.readTree(json);
        return jsonNode.get("contents").get("twoColumnSearchResultsRenderer").get("primaryContents").get("sectionListRenderer").get("contents").get(0).get("itemSectionRenderer").get("contents");
    }

}
