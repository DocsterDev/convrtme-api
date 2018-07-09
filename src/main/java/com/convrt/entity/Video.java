package com.convrt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    @Column(name = "id", length = 20)
    private String id;

    @NonNull
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @NonNull
    @Column(name = "owner", length = 30, nullable = false)
    private String owner;

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
    @Column(name = "duration", length = 15, nullable = false)
    private String duration;

    @JsonIgnore
    @ManyToMany(mappedBy = "videos")
    private List<Playlist> addedByPlaylists = Lists.newArrayList();

    @Transient
    private String publishedTimeAgo;

    @Transient
    private String viewCount;

    @Transient
    private Long playCount;

}
