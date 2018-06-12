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
public class VideoStreamInfoWS {

    public VideoStreamInfoWS(String source, Long size, String contentType, boolean audio) {
        this.source = source;
        this.size = size;
        this.contentType = contentType;
        this.audio = audio;
        this.sourceFetchedDate = Instant.now();
        if (StringUtils.isNotBlank(this.source)) {
            MultiValueMap<String, String> parameters =
                    UriComponentsBuilder.fromUriString(this.source).build().getQueryParams();
            List<String> param1 = parameters.get("expire");
            this.sourceExpireDate = Instant.ofEpochSecond(Long.valueOf(param1.get(0)));
            log.info("Successfully parsed expiration date for video: " + this.sourceExpireDate);
        }
    }

    private String source;
    private Instant sourceFetchedDate;
    private Instant sourceExpireDate;
    private Long size;
    private String contentType;
    private boolean audio;
    private VideoInfoWS videoInfo;
}
