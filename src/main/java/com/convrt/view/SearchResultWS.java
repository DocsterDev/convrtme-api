package com.convrt.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultWS {

    private String videoId;
    private String title;
    private String owner;
    private String viewCount;
    private String url;
    private String thumbnailUrl;
    private String duration;
    private String publishedTimeAgo;
    private boolean audio;

}
