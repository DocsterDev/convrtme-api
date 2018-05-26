package com.convrt.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YouTubeVideoInfoWS {
    private String id;
    private String title;
    private String owner;
    private String viewCount;
    private String duration;
    private Boolean newUpload;
}

