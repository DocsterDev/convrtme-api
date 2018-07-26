package com.convrt.api.controller;

import com.convrt.api.entity.Video;
import com.convrt.api.service.VideoService;
import com.convrt.api.service.StreamConversionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Slf4j
@RestController
@RequestMapping("/api/stream")
public class AudioStreamController {

    @Autowired
    private StreamConversionService youtubeStreamConversionService;
    @Autowired
    private VideoService videoService;

    @GetMapping("/{videoId}")
    public StreamingResponseBody handleRequest(@PathVariable("videoId") String videoId, HttpServletResponse response) {
        Video video = videoService.readVideoMetadata(videoId);
        response.setContentType("audio/webm");
        response.setHeader("Content-disposition", "inline; filename=output.webm");
        Thread.currentThread().setName("Thread-" + videoId);
        if (Thread.currentThread().isInterrupted()) {
            log.info("Thread " + Thread.currentThread().getName() + " Is Interrupted");
        }
        log.info("Thread Name: " + Thread.currentThread().getName());

        StreamingResponseBody streamingResponse = new StreamingResponseBody() {
            @Override
            public void writeTo(OutputStream outputStream) {
                Process p = null;
                p = youtubeStreamConversionService.convertVideo(video.getStreamUrl());
                try {
                    IOUtils.copyLarge(p.getInputStream(), outputStream);
                } catch (Exception e) {
                    throw new RuntimeException("Error streaming videoid " + videoId, e);
                } finally {
                    p.destroy();
                    IOUtils.closeQuietly(outputStream);
                }
                log.info("STREAM CLOSED!!!");
            }
        };

        return streamingResponse;
    }
}
