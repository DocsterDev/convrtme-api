
package com.convrt.controller;

import com.convrt.service.StreamMetadataService;
import com.convrt.view.VideoStreamMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/videos")
public class StreamMetadataController {

    @Autowired
    private StreamMetadataService youtubeStreamMetadataService;

    @PostMapping("{videoId}/metadata")
    public VideoStreamMetadata getStreamMetadata(@PathVariable("videoId") String videoId, @RequestBody VideoStreamMetadata videoStreamMetadata) {
        log.info("Metadata request for video {}", videoId);
        videoStreamMetadata.setVideoId(videoId);
        return youtubeStreamMetadataService.mapStreamData("12345", videoStreamMetadata);
    }



    }

