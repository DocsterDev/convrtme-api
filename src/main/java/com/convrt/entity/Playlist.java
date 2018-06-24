package com.convrt.entity;

import com.convrt.utils.JpaJsonConverter;
import com.convrt.view.VideoIdSet;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    //@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_playlist_user_uuid"))
    private User user;

}
