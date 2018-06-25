package com.convrt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Slf4j
@Entity
@Table(name = "context", indexes = {@Index(name = "context_token_idx0", columnList = "token"), @Index(name = "context_user_uuid_idx1", columnList = "user_uuid")})
public class Context extends BaseEntity {

    public Context () {
        this.uuid = UUID.randomUUID().toString();
    }

    @Column(name = "token", length = 100, nullable = false, updatable = false)
    private String token;

    @Column(name = "user_agent", length = 100)
    private String userAgent;

    @JsonIgnore
    @Column(name = "valid")
    private boolean valid;

    @JsonIgnore
    @Column(name = "last_login")
    private Instant lastLogin;

    // @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_context_user_uuid"))
    private User user;

//    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "context", orphanRemoval = true)
    private List<Log> logs;

    public void addLog(Log ctxLog){
        log.info("Adding log for context uuid = {}", this.uuid);
        if (logs == null) { logs  = Lists.newArrayList(); }
        ctxLog.setContext(this);
        this.logs.add(ctxLog);
    }

}
