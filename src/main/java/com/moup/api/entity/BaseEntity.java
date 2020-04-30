package com.moup.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "uuid", length = 36)
    public String uuid;

    @JsonIgnore
    @Column(name = "created_date", nullable = false, updatable = false)
    public Instant createdDate;

    @JsonIgnore
    @Column(name = "modified_date", nullable = false, updatable = false)
    public Instant modifiedDate;

    @PrePersist
    protected void prePersist() {
        Instant now = Instant.now();
        this.createdDate = now;
        this.modifiedDate = now;
    }

    @PreUpdate
    protected void preUpdate() {
        Instant now = Instant.now();
        if (this.createdDate == null) {
            this.createdDate = now;
        }
        this.modifiedDate = now;
    }

}
