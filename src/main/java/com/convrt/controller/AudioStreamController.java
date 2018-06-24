package com.convrt.controller;

import com.convrt.service.VideoService;
import com.convrt.service.StreamConversionService;
import com.convrt.view.VideoStreamMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.catalina.connector.OutputBuffer.DEFAULT_BUFFER_SIZE;


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
        VideoStreamMetadata videoStreamMetadata = videoService.readVideoByVideoId(videoId);
        response.setContentType("audio/webm");
        response.setHeader("Content-disposition", "inline; filename=output.webm");
        return new StreamingResponseBody() {
            @Override
            public void writeTo(OutputStream outputStream) {
                try {
                    InputStream is = youtubeStreamConversionService.convertVideo(videoStreamMetadata.getSource());
                    IOUtils.copyLarge(is, outputStream, new byte[DEFAULT_BUFFER_SIZE]);
                    outputStream.close();
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Cannot stream videoId=%s", videoId), e);
                }
            }
        };
    }
}
