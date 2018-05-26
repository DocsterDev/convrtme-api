
package com.convrt.controller;

import com.convrt.service.YouTubeDownloadService;
import com.convrt.view.YouTubeDownloadView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/youtube/videos")
public class YouTubeDownloadController {

    @Autowired
    private YouTubeDownloadService appManagedDownload;

    @GetMapping("{videoId}/download")
    public YouTubeDownloadView download(@PathVariable("videoId") String videoId) {
        log.info("Download request for video {}", videoId);
        return appManagedDownload.startDownload(String.format("https://www.youtube.com/watch?v=%s", videoId));
    }

}

