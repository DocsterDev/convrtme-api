package com.convrt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class YouTubeConversionService {

    private static final String MPEG_PATH_LINUX = "/usr/local/Cellar/ffmpeg/3.4.2/bin";

    public void convertVideo(String url) {

        final Path videoIn = Paths.get(url);
        final Path encodingFile = Paths.get("output.webm");
        final Path errorFile = Paths.get("error.txt");

        int retCode;

        try {
            Files.deleteIfExists(encodingFile);
            Files.deleteIfExists(errorFile);

            log.info("Video URL to be Converted: {}", videoIn.toString());

//            final ProcessBuilder pb
//                    = new ProcessBuilder("./ffmpeg",
//                    "-i", videoIn.toString(),
//                    "-vn",
//                    "-ab",
//                    "128k",
//                    "-ar",
//                    "44100",
//                    "-y",
//                    "-"
//            );
            log.info("URL: " + MPEG_PATH_LINUX + "/ffmpeg");
            final ProcessBuilder pb
                    = new ProcessBuilder(MPEG_PATH_LINUX + "/ffmpeg",
                    "-y",
                    "-i",
                    videoIn.toString(),
                    "output.mp3"
            );


            // NOTE: Ran: "brew reinstall ffmpeg --with-libvpx --with-libvorbis" on Mac to download necessary codecs
//            final ProcessBuilder pb
//                    = new ProcessBuilder(MPEG_PATH_LINUX + "/ffmpeg",
//                    "-i",
//                    videoIn.toString(),
//                    "-c:a",
//                    "libvorbis",
//                    "-b:a",
//                    "128k",
//                    "-vn",
//                    "-f",
//                    "webm",
//                    "-dash",
//                    "1",
//                    "output.webm"
//            );

            // ffmpeg -i input_audio.wav -c:a libvorbis -b:a 128k -vn -f webm -dash 1 output.webm

            pb.redirectErrorStream(true);
            pb.redirectError(errorFile.toFile());
            pb.redirectOutput(encodingFile.toFile());

            final Process p = pb.start();

            // CHECK FOR THIS!
            retCode = p.waitFor();

        } catch (Exception e) {
            // deal with e here
            e.printStackTrace();
        }

    }
}
