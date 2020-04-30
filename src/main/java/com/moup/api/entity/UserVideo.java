package com.moup.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Slf4j
@Data
@NoArgsConstructor
@Entity
@Table(name = "user_video", indexes = {@Index(name = "user_video_user_uuid_idx0", columnList = "user_uuid"), @Index(name = "user_video_video_id_idx0", columnList = "video_id"), @Index(name = "user_video_videos_order_idx0", columnList = "videos_order")})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserVideo implements Serializable {

    @Id
    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "user_uuid", length = 36, insertable = false, updatable = false)
    private String userUuid;

    @Column(name = "video_id", length = 36, insertable = false, updatable = false)
    private String videoId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_user_video_user_uuid"))
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "video_id", foreignKey = @ForeignKey(name = "fk_user_video_video_id"))
    private Video video;

    @Column(name = "videos_order")
    private int videosOrder;

    @Column(name = "viewed_date")
    private Instant viewedDate;

    @Column(name = "playhead_position")
    private Long playheadPosition;

//    @PrePersist
//    protected void onCreate() {
//        log.info("Setting timestamp");
//        this.viewedDate = Instant.now();
//    }
}

