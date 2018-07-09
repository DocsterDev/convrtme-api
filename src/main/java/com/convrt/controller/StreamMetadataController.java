
package com.convrt.controller;

import com.convrt.entity.Video;
import com.convrt.service.StreamMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/videos")
public class StreamMetadataController {

    @Autowired
    private StreamMetadataService streamMetadataService;

    @PostMapping("{videoId}/metadata")
    public Video getStreamMetadata(@PathVariable("videoId") String videoId, @RequestBody Video video) {
        log.info("Metadata request for video {}", videoId);
        video.setId(videoId);
        return streamMetadataService.mapStreamData(video);
    }

}
