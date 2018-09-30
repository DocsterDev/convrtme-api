package com.convrt.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
@Entity
@Table(name = "channel")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Channel {
    @Id
    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "name", length = 200)
    String name;

    @Column(name = "avatar_url", length = 300)
    String avatarUrl;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "channel", orphanRemoval = true)
    private Map<String, Subscription> subscriptions = Maps.newHashMap();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "channel", orphanRemoval = true)
    private List<Video> videos = Lists.newLinkedList();
}
