package com.convrt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "video", indexes = {@Index(name = "video_video_id_idx0", columnList = "video_id")})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Video extends BaseEntity {

    @Column(name = "video_id", length = 20)
    private String videoId;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "owner", length = 30)
    private String owner;

    @JsonIgnore
    @Column(name = "stream_url_date")
    private Instant streamUrlDate;

    @JsonIgnore
    @Column(name = "stream_url_expire_date")
    private Instant streamUrlExpireDate;

    @JsonIgnore
    @Column(name = "stream_url", length = 1000)
    private String streamUrl;

    @Column(name = "play_duration")
    private Long playDuration;

    @ManyToMany(mappedBy = "playlistVideos")
    private List<Playlist> addedByPlaylists = Lists.newArrayList();

//    @ManyToMany(mappedBy = "enabledRewards")
//    private List<Company> enabledByCompanies = Lists.newArrayList();

}
