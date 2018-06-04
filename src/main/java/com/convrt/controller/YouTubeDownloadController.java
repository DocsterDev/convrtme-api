
package com.convrt.controller;

import com.convrt.entity.Video;
import com.convrt.repository.VideoRepository;
import com.convrt.service.VideoService;
import com.convrt.service.YouTubeDownloadService;
import com.convrt.view.VideoStreamInfoWS;
import com.convrt.view.VideoInfoWS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/youtube/videos")
public class YouTubeDownloadController {

    @Autowired
    private YouTubeDownloadService youtubeDownloadService;


    @PostMapping("{videoId}/download")
    public VideoStreamInfoWS download(@PathVariable("videoId") String videoId, @RequestBody VideoInfoWS videoInfo) {
        log.info("Download request for video {}", videoId);
//        try {
//            Thread.sleep(5000);
//        } catch (Exception e) {
//
//        }
        VideoStreamInfoWS streamInfo = youtubeDownloadService.downloadAndSaveVideo("FAKE_USER_UUID", videoInfo);
        return streamInfo;
    }

}

