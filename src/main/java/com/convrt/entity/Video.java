package com.convrt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "video", indexes = {@Index(name = "video_video_id_idx0", columnList = "video_id"), @Index(name = "video_user_uuid_idx1", columnList = "user_uuid")})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Video extends BaseEntity {

    @Column(name = "video_id", length = 20)
    private String videoId;

    @Column(name = "user_uuid", length = 36)
    private String userUuid;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "owner", length = 30)
    private String owner;

    @JsonIgnore
    @Column(name = "stream_url_date")
    private Instant streamUrlDate;

    @JsonIgnore
    @Column(name = "stream_url_expire_date")
    private Instant streamUrlExpireDate;

    @JsonIgnore
    @Column(name = "stream_url", length = 1000)
    private String streamUrl;

    @Column(name = "last_played_date")
    private Instant lastPlayedDate;

    @Column(name = "play_duration")
    private Long playDuration;

    // TODO: Make this an @Formula
    @Formula("(select play_count.play_count)")
    private Long playCount;


}
