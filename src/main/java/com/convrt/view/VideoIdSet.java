package com.convrt.view;

import com.convrt.entity.Video;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Formula;

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
   // @Formula("(select * from table(video) v where videoId='videoId')")
    private Video video;
}
