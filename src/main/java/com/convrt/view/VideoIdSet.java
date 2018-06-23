package com.convrt.view;

import com.convrt.entity.Video;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoIdSet {
    @NonNull
    private Integer order;
    @NonNull
    private String videoId;
    // TODO: Do @Formula to retrieve videos instead of stupid logic
    private Video video;
}
