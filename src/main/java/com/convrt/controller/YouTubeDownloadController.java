
package com.convrt.controller;

import com.convrt.service.YouTubeDownloadService;
import com.convrt.view.VideoStreamMetadata;
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
    public VideoStreamMetadata download(@PathVariable("videoId") String videoId, @RequestBody VideoStreamMetadata videoStreamMetadata) {
        log.info("Download request for video {}", videoId);
        videoStreamMetadata.setVideoId(videoId);
        return youtubeDownloadService.mapStreamData("12345", videoStreamMetadata);
    }



    }

