package com.convrt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "auth", indexes = {@Index(name = "auth_token_idx0", columnList = "token"), @Index(name = "auth_user_uuid_idx0", columnList = "user_uuid")})
public class Auth extends BaseEntity {

    @JsonIgnore
    private String uuid;

    @Column(name = "token", length = 100, nullable = false, updatable = false)
    private String token;

    @JsonIgnore
    @Column(name = "valid")
    private boolean valid;

    @JsonIgnore
    @Column(name = "last_login")
    private Instant lastLogin;

    @JsonIgnore
    @Column(name = "user_agent", length = 100)
    private String userAgent;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_auth_user_uuid"))
    private User user;

}

