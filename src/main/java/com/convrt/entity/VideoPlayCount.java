package com.convrt.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "video_play_count", indexes = {@Index(name = "video_play_count_video_id_idx0", columnList = "video_id"), @Index(name = "video_play_count_user_uuid_idx1", columnList = "user_uuid")})
public class VideoPlayCount extends BaseEntity {

    @Column(name = "video_id", length = 20)
    private String videoId;

    @Column(name = "user_uuid", length = 36)
    private String userUuid;

    @Column(name = "play_count")
    private long playCount;

}
