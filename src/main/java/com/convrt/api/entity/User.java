package com.convrt.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "user",
        uniqueConstraints = {@UniqueConstraint(columnNames = "email")},
        indexes = {@Index(name = "user_email_idx0", columnList = "email"), @Index(name = "user_pin_idx1", columnList = "pin")})
public class User extends BaseEntity {

    public User (String email, String pin) {
        this.uuid = UUID.randomUUID().toString();
        this.pin = pin;
        this.email = email;
    }

    @NonNull
    @Column(name = "email", length = 50)
    private String email;

    @NonNull
    @JsonIgnore
    @Column(name = "pin", length = 4) // TODO: encrypt this
    private String pin;

    @Column(name = "ip", length = 50)
    private String ip;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "region", length = 100)
    private String region;

    @OrderColumn
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Playlist> playlists;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<PlayCount> playCounts = Lists.newArrayList();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Context> contexts;

    @JsonIgnore
    @OrderColumn
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_video", joinColumns = @JoinColumn(name = "user_uuid"), inverseJoinColumns = @JoinColumn(name = "video_id"))
    private List<Video> videos = Lists.newArrayList();

    @JsonIgnore
    public void iteratePlayCount(Video video) {
        for (PlayCount playCount: playCounts) {
            if (video.getId().equals(playCount.getVideo().getId())) {
                playCount.iterateNumPlays();
                return;
            }
        }
        PlayCount playCount = new PlayCount(video, this);
        playCounts.add(playCount);
    }

}
