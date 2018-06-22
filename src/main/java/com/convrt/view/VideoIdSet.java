package com.convrt.view;

import com.convrt.entity.Video;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoIdSet {
    private Integer order;
    private String videoId;
    private Video video;
}
