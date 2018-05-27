package com.convrt.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoInfoWS {
    private String id;
    private String title;
    private String owner;
    private String viewCount;
    private Long duration;
    private Instant currentTime;
    private String publishedTimeAgo;
    private Boolean newUpload;
}

