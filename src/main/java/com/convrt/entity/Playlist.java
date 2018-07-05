package com.convrt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Data
@NoArgsConstructor
@Entity
@Table(name = "playlist", indexes = {@Index(name = "playlist_user_id_idx0", columnList = "user_uuid")}, uniqueConstraints={@UniqueConstraint(columnNames = {"name", "user_uuid"})})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Playlist extends BaseEntity {

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "icon_color", length = 6)
    private String iconColor;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_playlist_user_uuid"))
    private User user;

    @OrderColumn
    @ManyToMany
    @JoinTable(name = "playlist_video_join_table", joinColumns = @JoinColumn(name = "playlist_uuid"), inverseJoinColumns = @JoinColumn(name = "video_uuid"), uniqueConstraints = @UniqueConstraint(name = "playlist_video_join_table_idx0", columnNames = {"playlist_uuid", "video_uuid"}))
    private List<Video> videos = Lists.newArrayList();

}
