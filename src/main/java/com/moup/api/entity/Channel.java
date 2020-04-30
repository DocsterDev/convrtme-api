package com.moup.api.entity;

import com.moup.api.utils.UUIDUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name = "channel", indexes = {@Index(name = "channel_name_idx0", columnList = "name"), @Index(name = "channel_channel_id_idx1", columnList = "channel_id")})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Channel {
    @Id
    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "avatar_url", length = 300)
    private String avatarUrl;

    @Column(name = "channel_id", length = 50)
    private String channelId;

    @Column(name = "is_subscribed")
    private boolean subscribed;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "channel", orphanRemoval = true)
    private List<Subscription> subscriptions = Lists.newLinkedList();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "channel", orphanRemoval = true)
    private List<Video> videos = Lists.newLinkedList();

    public Channel(String name) {
        this.name = name;
        this.uuid = UUIDUtils.generateUuid(name);
    }
}
