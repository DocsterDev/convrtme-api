
package com.convrt.controller;

import com.convrt.executor.YouTubeDownloadExecutor;
import com.convrt.service.AppManagedDownload;
import com.convrt.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/youtube/videos")
public class VideoDownloadController {

    @Autowired
    YouTubeDownloadExecutor downloadExecutor;
    @Autowired
    AppManagedDownload appManagedDownload;

    @Cacheable("video")
    @GetMapping("{videoId}/download")
    public ResponseWrapper download(@PathVariable("videoId") String videoId) {
        log.info("Download request for video {}", videoId);
        String audioUrl = appManagedDownload.startDownload(String.format("https://www.youtube.com/watch?v=%s", videoId));
        return new ResponseWrapper(audioUrl);
    }

}

