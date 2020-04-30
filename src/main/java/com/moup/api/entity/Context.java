package com.moup.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Slf4j
@Entity
@Table(name = "context", indexes = {@Index(name = "context_token_idx0", columnList = "token"), @Index(name = "context_user_agent_idx1", columnList = "user_agent")})
public class Context extends BaseEntity {

    public Context() {
        this.uuid = UUID.randomUUID().toString();
    }

    @Column(name = "token", length = 100, nullable = false)
    private String token;

    @Column(name = "user_agent", length = 200)
    private String userAgent;

    @Column(name = "ip", length = 50)
    private String ip;

    @Column(name = "isp", length = 50)
    private String isp;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "region", length = 50)
    private String region;

    @Column(name = "regionName", length = 50)
    private String regionName;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "countryCode", length = 10)
    private String countryCode;

    @Column(name = "latitude", length = 20)
    private String latitude;

    @Column(name = "longitude", length = 20)
    private String longitude;

    @Column(name = "timezone", length = 50)
    private String timezone;

    @Column(name = "zip", length = 20)
    private String zip;

    @Column(name = "expire_date")
    private Instant expireDate;

    @Column(name = "valid")
    private boolean valid;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_context_user_uuid"))
    private User user;
}
