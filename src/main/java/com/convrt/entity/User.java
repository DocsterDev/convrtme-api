package com.convrt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user",
        uniqueConstraints = {@UniqueConstraint(columnNames = "email")},
        indexes = {@Index(name = "user_email_idx0", columnList = "email"), @Index(name = "user_pin_idx1", columnList = "pin")})
public class User extends BaseEntity {

    @Email
    private String email;

    private String pin;

    @JsonIgnore
    private String userAgent;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<PlayCount> playCounts;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Playlist> playlists;

}
