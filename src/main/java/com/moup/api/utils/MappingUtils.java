package com.moup.api.utils;

import com.moup.api.entity.Video;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

public class MappingUtils {

    public static void findIsNew(JsonNode next, Video searchResult, JsonNode badges) {
        if (badges != null) {
            if (badges.size() > 0) {
                Iterator<JsonNode> badgeIter = next.get("badges").iterator();
                while(badgeIter.hasNext()){
                    JsonNode metadataNode = badgeIter.next().get("metadataBadgeRenderer");
                    if (metadataNode != null) {
                        JsonNode labelNode = metadataNode.get("label");
                        if(labelNode != null){
                            String label = labelNode.asText();
                            if ("New".equals(label)) {
                                searchResult.setNew(true);
                            }
                        }
                    }
                }
            }
        }
    }

}
