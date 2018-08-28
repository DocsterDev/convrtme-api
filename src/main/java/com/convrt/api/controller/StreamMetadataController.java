
package com.convrt.api.controller;

import com.convrt.api.entity.Video;
import com.convrt.api.service.StreamMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/api/videos")
public class StreamMetadataController {

    @Autowired
    private StreamMetadataService streamMetadataService;

    @GetMapping("{videoId}/metadata")
    public Video getStreamMetadata(@PathVariable("videoId") String videoId) {
        return streamMetadataService.fetchStreamUrl(videoId);
    }

    @PutMapping("{videoId}/metadata")
    public Video updateVideoMetadata(@PathVariable("videoId") String videoId, @RequestBody Video video) {
        video.setId(videoId);
        return streamMetadataService.updateVideoMetadata(video);
    }
}
