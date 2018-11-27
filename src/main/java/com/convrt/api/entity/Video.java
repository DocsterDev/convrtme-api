package com.convrt.api.entity;

import com.convrt.api.view.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Entity
@Table(name = "video", indexes = {@Index(name = "video_subscription_scanned_date_idx0", columnList = "subscription_scanned_date")})
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
    @Column(name = "duration")
    private String duration;

    @Column(name = "duration_sec")
    private Long durationSec;

    @Column(name = "description", length = 1000)
    private String description;

    @JsonIgnore
    @Column(name = "subscription_scanned_date")
    private Instant subscriptionScannedDate;

    @Column(name = "upload_date")
    private Instant uploadDate;

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
    @JsonView({View.PlaylistWithVideo.class, View.VideoWithPlaylist.class})
    private String owner;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "video", orphanRemoval = true)
    private Map<String, Stream> streams = Maps.newHashMap();

    @Transient
    private String thumbnailUrl;

    @Transient
    private String dateScanned;

    @Transient
    private String dateLastWatched;

    @Transient
    private boolean isNew;

    @Transient
    private boolean hasViewed;

    @Transient
    private String channelThumbnailUrl;

    @Transient
    private String channelId;

    public String getOwner() {
        if (this.owner == null) {
            this.owner = getChannel().getName();
        }
        return this.owner;
    }

}
