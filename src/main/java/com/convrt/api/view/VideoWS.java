package com.convrt.api.view;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VideoWS {
    private String id;
    private boolean success;
    private String streamUrl;
    private boolean audioOnly;
}
