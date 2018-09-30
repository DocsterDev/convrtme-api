package com.convrt.api.entity;

import com.convrt.api.utils.UUIDUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name = "channel")
public class Channel extends BaseEntity {

    public Channel(String name) {
        this.uuid = UUIDUtils.generateUuid(name);
        this.name = name;
    }

    @NonNull
    @Column(name = "name", length = 100, unique = true)
    String name;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "channel", orphanRemoval = true)
    private List<Video> videos;

    @JsonIgnore
    @ManyToMany(mappedBy = "channels", fetch = FetchType.LAZY)
    private List<User> subscribers = Lists.newArrayList();

}
