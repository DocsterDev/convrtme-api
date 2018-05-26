package com.convrt.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YouTubeStreamInfoWS {

    public YouTubeStreamInfoWS(String source, Long size, String contentType, boolean audio) {
        this.source = source;
        this.size = size;
        this.contentType = contentType;
        this.audio = audio;
    }

    private String source;
    private Long size;
    private String contentType;
    private boolean audio;
    private YouTubeVideoInfoWS videoInfo;
}
