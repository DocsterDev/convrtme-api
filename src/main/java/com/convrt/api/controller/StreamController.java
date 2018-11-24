
package com.convrt.api.controller;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Video;
import com.convrt.api.service.AudioExtractorService;
import com.convrt.api.service.RecommendedService;
import com.convrt.api.service.StreamService;
import com.convrt.api.service.VideoService;
import com.convrt.api.utils.UserAgentService;
import com.convrt.api.view.NowPlayingVideoWS;
import com.convrt.api.view.StreamWS;
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
    public StreamWS fetchMediaStreamUrl(@RequestHeader("User-Agent") String userAgent, @PathVariable("videoId") String videoId) {
        userAgentService.parseUserAgent(userAgent);
        return streamService.fetchStreamUrl(videoId, userAgentService.isChrome());
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
}