
package com.convrt.controller;

import com.convrt.service.YouTubeDownloadService;
import com.convrt.view.YouTubeStreamInfoWS;
import com.convrt.view.YouTubeVideoInfoWS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/youtube/videos")
public class YouTubeDownloadController {

    @Autowired
    private YouTubeDownloadService youtubeDownloadService;

    @PostMapping("{videoId}/download")
    public YouTubeStreamInfoWS download(@PathVariable("videoId") String videoId, @RequestBody YouTubeVideoInfoWS videoInfo) {
        log.info("Download request for video {}", videoId);
        YouTubeStreamInfoWS streamInfo = youtubeDownloadService.startDownload(videoId);
        streamInfo.setVideoInfo(videoInfo);
        return streamInfo;
    }

}

