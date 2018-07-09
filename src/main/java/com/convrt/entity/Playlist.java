package com.convrt.entity;

import com.convrt.view.View;
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
    //@JsonView(View.Playlist.class)
    @Column(name = "uuid", length = 36, nullable = false)
    private String uuid;

    //@JsonView(View.Playlist.class)
    @Column(name = "name", length = 100)
    private String name;

    //@JsonView(View.Playlist.class)
    @Column(name = "icon_color", length = 6)
    private String iconColor;

    @JsonIgnore
    @Column(name = "last_accessed")
    private Instant lastAccessed = Instant.now();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_playlist_user_uuid"))
    private User user;

    @OrderColumn
    @ManyToMany(fetch = FetchType.LAZY)
   // @JsonView(View.PlaylistWithVideo.class)
    @JoinTable(name = "playlist_video_join_table", joinColumns = @JoinColumn(name = "playlist_uuid"), inverseJoinColumns = @JoinColumn(name = "video_uuid"), uniqueConstraints = @UniqueConstraint(name = "playlist_video_join_table_idx0", columnNames = {"playlist_uuid", "video_uuid"}))
    private List<Video> videos = Lists.newArrayList();

}
