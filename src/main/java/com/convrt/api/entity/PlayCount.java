package com.convrt.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "play_count", indexes = {@Index(name = "play_count_video_id_idx0", columnList = "video_id"), @Index(name = "play_count_user_uuid_idx1", columnList = "user_uuid")})
public class PlayCount extends BaseEntity {

    public PlayCount() {
        this.uuid = UUID.randomUUID().toString();
    }

    public PlayCount(Video video, User user) {
        this.uuid = UUID.randomUUID().toString();
        this.video = video;
        this.user = user;
    }

    @JsonIgnore
    private String uuid;

    @Column(name = "num_plays")
    private long numPlays = 1;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_play_count_user_uuid"))
    private User user;

    @JsonIgnore
    public void iterateNumPlays(){
        this.numPlays += 1;
    }

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

}
