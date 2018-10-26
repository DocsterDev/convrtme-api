
package com.convrt.api.controller;

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
    @Autowired
    private RecommendedService recommendedService;

    @GetMapping("{videoId}/metadata")
    public NowPlayingVideoWS getStreamMetadata(@RequestHeader("User-Agent") String userAgent, @PathVariable("videoId") String videoId, @RequestParam(value = "token", required = false) String token) {
        userAgentService.parseUserAgent(userAgent);
        String extension = userAgentService.isChrome() ? "webm" : "m4a";
        StreamWS streamInfo = streamService.fetchStreamUrl(videoId, extension);
        NowPlayingVideoWS nowPlayingVideoWS = recommendedService.getRecommended(videoId);
        nowPlayingVideoWS.setStreamInfo(streamInfo);
        return nowPlayingVideoWS;
    }

    @GetMapping("{videoId}/metadata/prefetch")
    public StreamWS prefetchMediaStreamUrl(@RequestHeader("User-Agent") String userAgent, @PathVariable("videoId") String videoId) {
        userAgentService.parseUserAgent(userAgent);
        return audioExtractorService.extractAudio(videoId, userAgentService.isChrome() ? "webm" : "m4a");
    }

    @PutMapping("{videoId}/metadata")
    public void updateVideoWatched(@PathVariable("videoId") String videoId, @RequestHeader("token") String token) {
        videoService.updateVideoWatched(videoId, token);
    }
}
