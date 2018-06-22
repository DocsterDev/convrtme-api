package com.convrt.entity;

import com.convrt.utils.JpaJsonConverter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "playlist", indexes = {@Index(name = "playlist_user_id_idx0", columnList = "user_uuid")})
public class Playlist extends BaseEntity {

    @Column(name = "icon_color", length = 10)
    private String iconColor;

    @Column(name = "user_uuid", length = 36)
    private String userUuid;

    @Column(name = "video_list_json", columnDefinition = "TEXT")
    @Convert(converter = JpaJsonConverter.class)
    private List<String> videoIdList = Lists.newArrayList();



}
