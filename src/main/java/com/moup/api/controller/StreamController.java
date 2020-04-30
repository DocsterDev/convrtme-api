
package com.moup.api.controller;

import com.moup.api.service.AudioExtractorService;
import com.moup.api.service.StreamService;
import com.moup.api.service.VideoService;
import com.moup.api.utils.UserAgentService;
import com.moup.api.view.StreamWS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/videos")
public class StreamController {

    @Autowired
    private StreamService streamService;
    @Autowired
    private UserAgentService userAgentService;
    @Autowired
    private AudioExtractorService audioExtractorService;
    @Autowired
    private VideoService videoService;

    @GetMapping("/{videoId}/stream")
    public StreamWS fetchMediaStreamUrl(@RequestHeader("User-Agent") String userAgent, @RequestHeader(value="token", required=false) String token, @PathVariable("videoId") String videoId) {
        userAgentService.parseUserAgent(userAgent);
        return streamService.fetchStreamUrl(videoId, userAgentService.isChrome(), token);
    }

    @GetMapping("/{videoId}/metadata/prefetch")
    public StreamWS prefetchMediaStreamUrl(@RequestHeader("User-Agent") String userAgent, @PathVariable("videoId") String videoId) {
        userAgentService.parseUserAgent(userAgent);
        return audioExtractorService.extractAudio(videoId, userAgentService.isChrome());
    }

    @PutMapping("/{videoId}/metadata")
    public void updateVideoWatched(@PathVariable("videoId") String videoId, @RequestHeader("token") String token) {
        videoService.updateVideoWatched(videoId, token);
    }

    @PutMapping("/{videoId}/position")
    public void updatePlayheadPosition(@RequestHeader("token") String token, @PathVariable("videoId") String videoId, @RequestParam("position") Long position) {
        videoService.updatePlayheadPosition(token, videoId, position);
    }

}
