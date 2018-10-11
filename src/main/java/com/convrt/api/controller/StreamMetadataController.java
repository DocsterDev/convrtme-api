
package com.convrt.api.controller;

import com.convrt.api.entity.Video;
import com.convrt.api.service.StreamMetadataService;
import com.convrt.api.view.VideoWS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/videos")
public class StreamMetadataController {

    @Autowired
    private StreamMetadataService streamMetadataService;

    @GetMapping("{videoId}/metadata")
    public VideoWS getStreamMetadata(@PathVariable("videoId") String videoId, @RequestParam(value = "token", required = false) String token) {
        Video video = streamMetadataService.fetchStreamUrl(videoId, token);
        VideoWS videoWs = new VideoWS();
        videoWs.setId(videoId);
        videoWs.setStreamUrl(video.getStreamUrl());
        return videoWs;
    }
}
