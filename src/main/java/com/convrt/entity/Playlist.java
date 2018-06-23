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

@Data
@Entity
@NoArgsConstructor
@Slf4j
@Table(name = "playlist", indexes = {@Index(name = "playlist_user_id_idx0", columnList = "user_uuid")})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Playlist extends BaseEntity {

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "icon_color", length = 10)
    private String iconColor;

    @Column(name = "user_uuid", length = 36)
    private String userUuid;

    @Column(name = "video_list_json", columnDefinition = "TEXT")
    @Convert(converter = JpaJsonConverter.class)
    private List<VideoIdSet> videos;

    @JsonIgnore
    public List<String> getVideoIdList(){
        if (videos == null) {
            log.error("No video ids found for this playlist");
        }
        return videos.stream().map(VideoIdSet::getVideoId).collect(Collectors.toList());
    }

}
