
package com.convrt.api.controller;

import com.convrt.api.entity.Video;
import com.convrt.api.service.ContextService;
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
    @Autowired
    private ContextService contextService;

    @PostMapping("{videoId}/metadata")
    public Video getStreamMetadata(@RequestHeader(value = "token") String token, @PathVariable("videoId") String videoId, @RequestBody Video video) {
        String userUuid = null;
//        if (token != null) {
//            Context context = contextService.validateContext(token);
//            userUuid = context.getUserUuid();
//        }
        video.setId(videoId);
        return streamMetadataService.mapStreamData(video, userUuid);
    }

}
