package com.convrt.view;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YouTubeDownloadView {
    private String url;
    private boolean audioOnly;
}
