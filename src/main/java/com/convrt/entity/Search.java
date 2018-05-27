package com.convrt.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.Instant;

@Data
@Entity
@Table(name = "search", indexes = {@Index(name = "search_query_idx0", columnList = "query"), @Index(name = "search_search_uuid_idx0", columnList = "search_uuid")})
public class Search extends BaseEntity {

    @Column(name = "search_uuid", length = 36)
    private String searchUuid;

    @Column(name = "query", length = 100)
    private String query;

    @Column(name = "query_date", columnDefinition = "DATETIME")
    private Instant queryDate;

    @Column(name = "video_id", length = 20)
    private String videoId;


    // Ideally will make a many to many relationship to video and search query

//    @Column(name = "video_results_list", columnDefinition = "TEXT")
//    private List<VideoInfoWS> videoResultsList;

}

