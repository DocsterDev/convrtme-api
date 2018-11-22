package com.convrt.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Data
@NoArgsConstructor
@Entity
@Table(name = "stream", indexes = {@Index(name = "stream_video_id_idx0", columnList = "video_id,extension")})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Stream extends BaseEntity{



    @Column(name = "stream_url_date")
    private Instant streamUrlDate;

    @Column(name = "stream_url_expire_date")
    private Instant streamUrlExpireDate;

    @Column(name = "stream_url", length = 1000)
    private String streamUrl;

    @Column(name = "extension", length = 5)
    private String extension;

    @Column(name = "is_audio_only")
    private boolean audioOnly;

    @Column(name = "is_matches_extension")
    private boolean matchesExtension;

    @Column(name = "abr")
    private float abr;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", foreignKey = @ForeignKey(name = "fk_stream_video_id"))
    private Video video;

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
        this.audioOnly = false;
        if (streamUrl != null) {
            MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(streamUrl).build().getQueryParams();
            List<String> expire = parameters.get("expire");
            this.streamUrlExpireDate = Instant.ofEpochSecond(Long.valueOf(expire.get(0)));
            this.streamUrlDate = Instant.now();
            List<String> mime = parameters.get("mime");
            if (!mime.isEmpty()) {
                String mimeStr = mime.get(0);
                if (StringUtils.isNotBlank(mimeStr)) {
                    this.audioOnly = mimeStr.contains("audio");
                    if (StringUtils.isNotBlank(this.extension)) {
                        this.matchesExtension = this.audioOnly && mimeStr.contains(this.extension.equals("m4a") ? "mp4":this.extension);
                    }
                }
            }
            log.info("Setting audio only: {} to mime type: {}", this.audioOnly, mime.get(0));
        }
    }
}
