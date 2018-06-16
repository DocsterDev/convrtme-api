package com.convrt.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "metadata")
public class Metadata extends BaseEntity {

    @Column(name = "user_uuid", length = 36)
    String userUuid;

    @Column(name = "title", length = 36)
    String title;
    String conversionFrom;
    String conversionTo;
    boolean uploadComplete;
    boolean conversionComplete;
    Date uploadStarted;
    Date uploadCompleted;
    Date conversionStarted;
    Date conversionCompleted;

}
