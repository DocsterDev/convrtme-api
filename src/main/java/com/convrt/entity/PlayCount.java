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
@Table(name = "play_count", indexes = {@Index(name = "play_count_video_id_idx0", columnList = "video_id"), @Index(name = "play_count_user_uuid_idx1", columnList = "user_uuid")})
public class PlayCount extends BaseEntity {

    @Column(name = "video_id", length = 20)
    private String videoId;

    @Column(name = "user_uuid", length = 36)
    private String userUuid;

    @Column(name = "num_plays")
    private long numPlays = 0;

    public void iterateNumPlays(){
        this.numPlays += 1;
    }

}
