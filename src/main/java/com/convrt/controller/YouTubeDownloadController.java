
package com.convrt.controller;

import com.convrt.entity.Video;
import com.convrt.repository.VideoRepository;
import com.convrt.service.YouTubeDownloadService;
import com.convrt.view.VideoStreamInfoWS;
import com.convrt.view.VideoInfoWS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/youtube/videos")
public class YouTubeDownloadController {

    @Autowired
    private YouTubeDownloadService youtubeDownloadService;
    @Autowired
    private VideoRepository videoRepository;

    @PostMapping("{videoId}/download")
    public VideoStreamInfoWS download(@PathVariable("videoId") String videoId, @RequestBody VideoInfoWS videoInfo) {
        log.info("Download request for video {}", videoId);
        VideoStreamInfoWS streamInfo = youtubeDownloadService.startDownload(videoId);
        streamInfo.setVideoInfo(videoInfo);
        Video video = new Video();
        video.setUuid(UUID.randomUUID().toString());
        video.setTitle(videoInfo.getTitle());
        video.setOwner(videoInfo.getOwner());
        video.setPublishedTimeAgo(videoInfo.getPublishedTimeAgo());
        video.setPlayDuration(videoInfo.getDuration());
        video.setStreamUrl(streamInfo.getSource());
        video.setDataSize(streamInfo.getSize());
        video.setStreamUrlDate(Instant.now());
        video.setAudioOnly(streamInfo.isAudio());
        video.setVideoId(videoInfo.getId());
        video.setStreamUrlExpireDate(streamInfo.getSourceExpireDate());

        videoRepository.save(video);
        return streamInfo;
    }

}

