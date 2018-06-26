package com.convrt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "play_count", indexes = {@Index(name = "play_count_video_id_idx0", columnList = "video_id"), @Index(name = "play_count_user_uuid_idx1", columnList = "user_uuid")})
public class PlayCount extends BaseEntity {

    @JsonIgnore
    private String uuid;

    @Column(name = "video_id", length = 20)
    private String videoId;

    @Column(name = "num_plays")
    private long numPlays = 0;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_play_count_user_uuid"))
    private User user;

    @JsonIgnore
    public void iterateNumPlays(){
        this.numPlays += 1;
    }

}
