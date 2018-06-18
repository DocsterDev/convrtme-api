package com.convrt.controller;

import com.convrt.service.YouTubeConversionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.catalina.connector.OutputBuffer.DEFAULT_BUFFER_SIZE;


@Slf4j
@RestController
@RequestMapping("/api/stream")
public class AudioStreamController {

    @Autowired
    private YouTubeConversionService youtubeConversionService;

    // private static Map<String, String> streamUrlMap;

    @GetMapping("/{videoId}")
    public StreamingResponseBody handleRequest(@PathVariable("videoId") String videoId, HttpServletResponse response) {
        String url = "https:/r5---sn-n4v7sn7z.googlevideo.com/videoplayback?mn=sn-n4v7sn7z%2Csn-a5meknsd&mm=31%2C26&ip=73.170.141.95&gir=yes&key=yt6&pl=23&mime=audio%2Fwebm&id=o-AJEijijLaIMoZNLzVoPRoEHcLwaNXKP-razb0jkly0X0&mv=m&sparams=clen%2Cdur%2Cei%2Cgcr%2Cgir%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Ckeepalive%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpl%2Crequiressl%2Csource%2Cexpire&mt=1529262620&ms=au%2Conr&fvip=5&lmt=1528694004558766&c=WEB&ipbits=0&clen=153444060&dur=10146.981&initcwndbps=1751250&source=youtube&requiressl=yes&signature=4F7082B95C625BFC66482A054EED0A8FEF702E9D.34BB2ACDB7C840282BB6C042237C4200F997401C&itag=251&keepalive=yes&gcr=us&ei=iLImW4yBHYiM_APvi5moBw&expire=1529284328&signature=4F7082B95C625BFC66482A054EED0A8FEF702E9D.34BB2ACDB7C840282BB6C042237C4200F997401C";
        response.setContentType("audio/webm");
        response.setHeader("Content-disposition", "inline; filename=output.webm");
        return new StreamingResponseBody() {
            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                InputStream is = youtubeConversionService.convertVideo(url);
                IOUtils.copyLarge(is, outputStream, new byte[DEFAULT_BUFFER_SIZE]);
                outputStream.close();
            }
        };
    }
}
