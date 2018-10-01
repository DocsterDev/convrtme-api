package com.convrt.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@NoArgsConstructor
@Data
@Entity
@Table(name = "subscription", uniqueConstraints = {@UniqueConstraint(columnNames = {"channel_uuid", "user_uuid"})}, indexes = {@Index(name = "subscription_channel_idx0", columnList = "channel_uuid"), @Index(name = "subscription_user_uuid_idx1", columnList = "user_uuid")})
public class Subscription extends BaseEntity {
    @Column(name = "subscribedDate")
    private Instant subscribedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "channel_uuid", foreignKey = @ForeignKey(name = "fk_subscription_channel_uuid"))
    private Channel channel;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_subscription_user_uuid"))
    private User user;
}
