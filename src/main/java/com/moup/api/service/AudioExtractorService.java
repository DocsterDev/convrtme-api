package com.moup.api.service;

import com.moup.api.view.StreamWS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class AudioExtractorService {

    public StreamWS extractAudio(String videoId, boolean isChrome) {
        ProcessBuilder pb = buildProcess(videoId, isChrome);
        String output = null;
        try {
            Process p = pb.start();
            try (InputStream is = p.getInputStream(); InputStream es = p.getErrorStream();) {
                String error = IOUtils.toString(es, "UTF-8");
                output = IOUtils.toString(is, "UTF-8");
                StreamWS streamWS = new StreamWS();
                if (StringUtils.isBlank(output)) {
                    streamWS.setId(videoId);
                    return streamWS;
                }
                streamWS.setId(videoId);
                return streamWS;
            } catch (IOException e) {
                throw new RuntimeException(String.format("Error executing YouTube-DL to extract video id for %s", videoId), e);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error extracting audio", e));
        }
    }

    public ProcessBuilder buildProcess(String videoId, boolean isChrome) {
        ProcessBuilder pb = new ProcessBuilder("youtube-dl",
                "--quiet",
                "--simulate",
                "--dump-single-json",
                 "-f",
                 "bestaudio",
                "--",
                videoId
        );
        log.info("Command ::: {}", StringUtils.join(pb.command(), StringUtils.SPACE));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
        return pb;
    }
}
