package com.convrt.api.entity;

import com.convrt.api.view.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

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
    @Column(name = "duration", length = 15)
    private String duration;

    @JsonIgnore
    @Column(name = "subscription_scanned_date")
    private Instant subscriptionScannedDate;

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

    @Transient
    private String thumbnailUrl;

    @Transient
    private String dateScanned;

    @Transient
    private boolean isNew;

    @Transient
    private boolean hasViewed;

    @Transient
    private String channelThumbnailUrl;

    public String getOwner() {
        if (this.channel != null) {
            return this.channel.getName();
        }
        return "Unknown";
    }

}
