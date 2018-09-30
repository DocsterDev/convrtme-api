package com.convrt.api.entity;

import com.convrt.api.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Fetch;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Slf4j
@Entity
@Table(name = "context", indexes = {@Index(name = "context_token_idx0", columnList = "token"), @Index(name = "context_user_agent_idx1", columnList = "user_agent"), @Index(name = "context_valid_idx2", columnList = "valid"),  @Index(name = "context_user_uuid_idx3", columnList = "user_uuid")})
public class Context extends BaseEntity {

//    @JsonIgnore
//    @Transient
//    @Autowired
//    private UserService userService;

    public Context () {
        this.uuid = UUID.randomUUID().toString();
    }

    @Column(name = "token", length = 100, nullable = false)
    private String token;

    @Column(name = "user_agent", length = 200)
    private String userAgent;

    @JsonIgnore
    @Column(name = "valid")
    private boolean valid;

    @JsonIgnore
    @Column(name = "last_login")
    private Instant lastLogin;

    @JsonIgnore
    @Column(name = "expire_date")
    private Instant expireDate;

    @JsonIgnore
    @Column(name = "user_uuid", length = 36, insertable = false, updatable = false)
    private String userUuid;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_context_user_uuid"))
    private User user;

//    public User getUser() {
//        return userService.readUser(getUserUuid());
//    }
//
//    public void setUser(User user) {
//        setUserUuid(user.getUuid());
//    }


}
