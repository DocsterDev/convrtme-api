package com.convrt.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class YouTubeConversionService {

    private static final String MPEG_PATH_LINUX = "/usr/local/Cellar/ffmpeg/3.4.2/bin";

    public InputStream convertVideo(String url) {

        final Path videoIn = Paths.get(url);

        // https://trac.ffmpeg.org/wiki/AudioChannelManipulation

        // Final works
        final ProcessBuilder pb
                = new ProcessBuilder(MPEG_PATH_LINUX + "/ffmpeg",
                "-i", videoIn.toString(),
                "-progress",
                "progress",
                "-vn",
                "-ab",
                "16k",
                "-ar",
                "11025",
                "-y",
                "-f",
                "webm",
                "-"
        );
        //pb.redirectErrorStream(true);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);

        Process p;
        try {
            p = pb.start();
        } catch (Exception e) {
            throw new RuntimeException("Cannot start audio conversion process");
        }
        return p.getInputStream();
    }

}
