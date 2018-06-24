package com.convrt.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.List;

@Slf4j
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoStreamMetadata {

    public VideoStreamMetadata(String source, Long length, String contentType, boolean audio) {
        this.source = source;
        this.length = length;
        this.contentType = contentType;
        this.audio = audio;
        this.sourceFetchedDate = Instant.now();
        if (StringUtils.isNotBlank(this.source)) {
            MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(this.source).build().getQueryParams();
            List<String> param1 = parameters.get("expire");
            this.sourceExpireDate = Instant.ofEpochSecond(Long.valueOf(param1.get(0)));
        }
    }

    private String source;
    private Instant sourceFetchedDate;
    private Instant sourceExpireDate;
    private Long length;
    private String contentType;
    private boolean audio;

    /* Video Info */
    private String videoId;
    private String title;
    private String owner;
    private String viewCount;
    private Long playCount;
    private Long duration;
    private Instant currentTime;
    private String publishedTimeAgo;
    private Boolean newUpload;

}
