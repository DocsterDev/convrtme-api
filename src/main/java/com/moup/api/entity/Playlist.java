package com.convrt.api.entity;

import com.convrt.api.view.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Data
@NoArgsConstructor
@Entity
@Table(name = "playlist", indexes = {@Index(name = "playlist_user_id_idx0", columnList = "user_uuid")}, uniqueConstraints={@UniqueConstraint(columnNames = {"name", "user_uuid"})})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Playlist extends BaseEntity {

    public Playlist (String name, String iconColor, User user) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.iconColor = iconColor;
        this.user = user;
    }

    @Id
    @JsonView({View.Playlist.class, View.VideoWithPlaylist.class})
    @Column(name = "uuid", length = 36, nullable = false)
    private String uuid;

    @JsonView({View.Playlist.class, View.VideoWithPlaylist.class})
    @Column(name = "name", length = 100)
    private String name;

    @JsonView({View.Playlist.class, View.VideoWithPlaylist.class})
    @Column(name = "icon_color", length = 6)
    private String iconColor;

    @JsonIgnore
    @Column(name = "last_accessed")
    private Instant lastAccessed = Instant.now();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_playlist_user_uuid"))
    private User user;

    @JsonIgnore
    @OrderColumn
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "playlist_video", joinColumns = @JoinColumn(name = "playlist_uuid"), inverseJoinColumns = @JoinColumn(name = "video_id"))
    private List<Video> videos = Lists.newArrayList();

}
