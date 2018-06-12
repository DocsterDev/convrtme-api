package com.convrt.entity;

import com.convrt.view.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @JsonView(View.BaseView.class)
    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "created_date", columnDefinition = "DATETIME", nullable = false, updatable = false)
    private Instant createdDate;

    @Column(name = "modified_date", columnDefinition = "DATETIME")
    private Instant modifiedDate;

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
