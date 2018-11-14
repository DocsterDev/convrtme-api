package com.convrt.api.service;

import com.convrt.api.view.StreamWS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class AudioExtractorService {

    public StreamWS extractAudio(String videoId, String ext, String userAgent) {
        ProcessBuilder pb = buildProcess(videoId, ext, userAgent);
        String output = null;
        try {
            Process p = pb.start();
            try (InputStream is = p.getInputStream(); InputStream es = p.getErrorStream();) {
                String error = IOUtils.toString(es, "UTF-8");
                output = IOUtils.toString(is, "UTF-8");
                StreamWS videoWS = new StreamWS();
                if (StringUtils.isBlank(output)) {
                    videoWS.setId(videoId);
                    videoWS.setSuccess(false);
                    videoWS.setAudioOnly(false);
                    return videoWS;
                }
                videoWS.setId(videoId);
                videoWS.setStreamUrl(output);
                videoWS.setSuccess(true);
                videoWS.setAudioOnly(true);
                return videoWS;
            } catch (IOException e) {
                throw new RuntimeException(String.format("Error executing YouTube-DL to extract video id for %s", videoId), e);
            }
        } catch (Exception e) {
            log.error("Error fetching audio url for videoId {} and file extension {}: {}", videoId, ext, output);
            StreamWS videoWS = new StreamWS();
            videoWS.setId(videoId);
            videoWS.setStreamUrl(null);
            videoWS.setSuccess(false);
            videoWS.setAudioOnly(false);
            return videoWS;
            //throw new RuntimeException(String.format("Error fetching audio url for videoId %s and file extension %s: %s", videoId, ext, output));
        }
    }

    public ProcessBuilder buildProcess(String videoId, String ext, String userAgent) {
        ext = null;
        ProcessBuilder pb = new ProcessBuilder("youtube-dl",
                "--quiet",
                "--simulate",
//                "--user-agent",
//                userAgent,
                "--dump-single-json",
                "-f",
                "bestaudio" + (StringUtils.isNotBlank(ext) ? "[ext=" + ext + "]" : StringUtils.EMPTY),
                "--",
                videoId
        );
        log.info("Command ::: {}", StringUtils.join(pb.command(), StringUtils.SPACE));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
        return pb;
    }
}
