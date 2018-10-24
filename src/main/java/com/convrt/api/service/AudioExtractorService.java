package com.convrt.api.service;

import com.convrt.api.view.VideoWS;
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

    @Cacheable("streamUrl")
    public VideoWS extractAudio(String videoId, String ext) {
        ProcessBuilder pb = buildProcess(videoId, ext);
        String output = null;
        try {
            Process p = pb.start();
            try (InputStream is = p.getInputStream(); InputStream es = p.getErrorStream();) {
                String error = IOUtils.toString(es, "UTF-8");
                output = IOUtils.toString(is, "UTF-8");
                VideoWS videoWS = new VideoWS();
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
            VideoWS videoWS = new VideoWS();
            videoWS.setId(videoId);
            videoWS.setStreamUrl(null);
            videoWS.setSuccess(false);
            videoWS.setAudioOnly(true);
            return videoWS;
            //throw new RuntimeException(String.format("Error fetching audio url for videoId %s and file extension %s: %s", videoId, ext, output));
        }
    }

    private ProcessBuilder buildProcess(String videoId, String ext) {
        return new ProcessBuilder("youtube-dl",
                "--quiet",
                "--simulate",
                "--get-url",
                "-f",
                "bestaudio" + (StringUtils.isNotBlank(ext) ? "[ext=" + ext + "]" : StringUtils.EMPTY),
                "--",
                videoId
        );
    }


}
