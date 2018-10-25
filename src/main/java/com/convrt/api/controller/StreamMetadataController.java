
package com.convrt.api.controller;

import com.convrt.api.service.AudioExtractorService;
import com.convrt.api.service.StreamMetadataService;
import com.convrt.api.utils.UserAgentService;
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
    @Autowired
    private UserAgentService userAgentService;
    @Autowired
    private AudioExtractorService audioExtractorService;

    @GetMapping("{videoId}/metadata")
    public VideoWS getStreamMetadata(@PathVariable("videoId") String videoId, @RequestParam(value = "token", required = false) String token) {
        return streamMetadataService.fetchStreamUrl(videoId, token);
    }

    @GetMapping("{videoId}/metadata/prefetch")
    public VideoWS prefetchMediaStreamUrl(@RequestHeader("User-Agent") String userAgent, @PathVariable("videoId") String videoId) {
        userAgentService.parseUserAgent(userAgent);
        return audioExtractorService.extractAudio(videoId, userAgentService.isChrome() ? "webm" : "m4a");
    }

    @PutMapping("{videoId}/metadata")
    public void updateVideoWatched(@PathVariable("videoId") String videoId, @RequestHeader("token") String token) {
        streamMetadataService.updateVideoWatched(videoId, token);
    }
}
