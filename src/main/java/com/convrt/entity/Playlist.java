package com.convrt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;

@Slf4j
@Data
@NoArgsConstructor
@Entity
@Table(name = "playlist", indexes = {@Index(name = "playlist_user_id_idx0", columnList = "user_uuid")})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Playlist extends BaseEntity {

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "icon_color", length = 10)
    private String iconColor;

//    @ManyToMany(mappedBy = "playlistVideos")
//    private List<Video> videos;

    @ManyToMany
    @JoinTable(name = "playlist_video_join_table", joinColumns = @JoinColumn(name = "playlist_uuid"), inverseJoinColumns = @JoinColumn(name = "video_id"), uniqueConstraints = @UniqueConstraint(name = "playlist_video_join_table_idx0", columnNames = { "playlist_uuid", "video_id" }))
    private List<Video> playlistVideos = Lists.newArrayList();

//    @ManyToMany
//    @JoinTable(name = "company_reward_join_table", joinColumns = @JoinColumn(name = "company_uuid"), inverseJoinColumns = @JoinColumn(name = "reward_uuid"), uniqueConstraints = @UniqueConstraint(name = "company_reward_join_table_idx0", columnNames = { "company_uuid", "reward_uuid" }))
//    private List<Reward> enabledRewards = Lists.newArrayList();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "playlist", orphanRemoval = true)
    private List<Video> videos;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_playlist_user_uuid"))
    private User user;

}
