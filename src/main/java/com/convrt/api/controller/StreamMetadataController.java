
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

    @PostMapping("{videoId}/metadata")
    public Video getStreamMetadata(@PathVariable("videoId") String videoId, @RequestBody Video video) {
        video.setId(videoId);
        Video vid = streamMetadataService.mapStreamData(video);
        if (vid.getStreamUrl() != null) {
            byte[] encodedUrl = Base64.getEncoder().encode(vid.getStreamUrl().getBytes());
            vid.setEncodedStreamUrl(new String(encodedUrl));
        }
        return vid;
    }
}
