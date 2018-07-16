package com.convrt.entity;

import com.convrt.view.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "video")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Video {

    @Id
    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    @Column(name = "id", length = 20)
    private String id;

    @NonNull
    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @NonNull
    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    @Column(name = "owner", length = 30, nullable = false)
    private String owner;

    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    @Column(name = "thumbnail_url", length = 300)
    private String thumbnailUrl;

    @JsonIgnore
    @Column(name = "stream_url_date")
    private Instant streamUrlDate;

    @JsonIgnore
    @Column(name = "stream_url_expire_date")
    private Instant streamUrlExpireDate;

    @JsonIgnore
    @Column(name = "stream_url", length = 1000)
    private String streamUrl;

    @NonNull
    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    @Column(name = "duration", length = 15, nullable = false)
    private String duration;

    @JsonView(View.VideoWithPlaylist.class)
    @ManyToMany(mappedBy = "videos")
    private List<Playlist> addedByPlaylists = Lists.newArrayList();

    @Transient
    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    private String publishedTimeAgo;

    @Transient
    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    private String viewCount;

}
