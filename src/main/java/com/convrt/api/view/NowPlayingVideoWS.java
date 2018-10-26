package com.convrt.api.view;

import com.convrt.api.entity.Video;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NowPlayingVideoWS {
    private String title;
    private String description;
    private String publishedDate;
    private String viewCount;
    private String shortViewCount;
    private String category;
    private StreamWS streamInfo;
    private Video nextUpVideo = new Video();
    private List<Video> recommendedVideos = Lists.newLinkedList();
}
