package com.convrt.api.entity;

import com.convrt.api.view.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.*;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "video")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Video {

    @Id
    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    @Column(name = "id", length = 20)
    private String id;

    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    @Column(name = "title", length = 100)
    private String title;

    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    @Column(name = "owner", length = 100)
    private String owner;

    @JsonIgnore
    @Column(name = "stream_url_date")
    private Instant streamUrlDate;

    @JsonIgnore
    @Column(name = "stream_url_expire_date")
    private Instant streamUrlExpireDate;

    @Column(name = "stream_url", length = 1000)
    private String streamUrl;

    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    @Column(name = "duration", length = 15)
    private String duration;

//    @JsonView(View.VideoWithPlaylist.class)
//    @ManyToMany(mappedBy = "videos")
//    private List<Playlist> addedByPlaylists = Lists.newArrayList();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_uuid", foreignKey = @ForeignKey(name = "fk_video_channel_uuid"))
    private Channel channel;

    @Transient
    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    private String publishedTimeAgo;

    @Transient
    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    private String viewCount;

    @Transient
    private String encodedStreamUrl;

    @Transient
    private String thumbnailUrl;

    @Transient
    private boolean isNew;

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
        if (streamUrl != null) {
            MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(streamUrl).build().getQueryParams();
            List<String> param1 = parameters.get("expire");
            this.streamUrlExpireDate = Instant.ofEpochSecond(Long.valueOf(param1.get(0)));
            this.streamUrlDate = Instant.now();
        }
    }
}
