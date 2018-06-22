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

    public InputStream convertVideo(String url) {

        // https://trac.ffmpeg.org/wiki/AudioChannelManipulation

        // Final works
        final ProcessBuilder pb
                = new ProcessBuilder("./ffmpeg.exe",
                "-i", url,
                "-progress",
                "progress",
                "-vn",
                "-c:a",
                "libopus",
                "-b:a",
                "16k",
                "-ar",
                "8000", // 48000 24000 16000 12000 8000
                "-compression_level",
                "10",
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
