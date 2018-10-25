package com.convrt.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "stream", indexes = {@Index(name = "stream_video_id_idx0", columnList = "video_id,extension")})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Stream extends BaseEntity{

    @Column(name = "video_id", length = 20)
    private String videoId;

    @Column(name = "stream_url_date")
    private Instant streamUrlDate;

    @Column(name = "stream_url_expire_date")
    private Instant streamUrlExpireDate;

    @Column(name = "stream_url", length = 1000)
    private String streamUrl;

    @Column(name = "extension", length = 5)
    private String extension;

    @Column(name = "is_audio_only")
    private Boolean audioOnly;

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
        if (streamUrl != null) {
            MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(streamUrl).build().getQueryParams();
            List<String> param1 = parameters.get("expire");
            this.streamUrlExpireDate = Instant.ofEpochSecond(Long.valueOf(param1.get(0)));
            this.streamUrlDate = Instant.now();
        }
    }
}
