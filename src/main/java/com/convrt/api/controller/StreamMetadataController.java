
package com.convrt.api.controller;

import com.convrt.api.entity.Video;
import com.convrt.api.service.StreamMetadataService;
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
    public Video getStreamMetadata(@PathVariable("videoId") String videoId, @RequestParam(value = "token", required = false) String token) {
        return streamMetadataService.fetchStreamUrl(videoId, token);
    }
}
