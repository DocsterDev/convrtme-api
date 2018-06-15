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

        // https://trac.ffmpeg.org/wiki/AudioChannelManipulation

        int retCode;

        try {
            Files.deleteIfExists(encodingFile);
            Files.deleteIfExists(errorFile);

            log.info("Video URL to be Converted: {}", videoIn.toString());

            // Final works
            final ProcessBuilder pb
                    = new ProcessBuilder(MPEG_PATH_LINUX + "/ffmpeg",
                    "-i", videoIn.toString(),
                    "-vn",
                    "-ab",
                    "64k", //-vn -ab 128k -ar 44100 -y
                    "-ar",
                    "22050",
                    "-y",
                    "output3.mp3"
            );

//            final ProcessBuilder pb
//                    = new ProcessBuilder(MPEG_PATH_LINUX + "/ffmpeg",
//                    "-y",
//                    "-i",
//                    videoIn.toString(),
//                    "-f",
//                    "mp3", //ffmpeg -i "movie.avi" -y -f flv -ar 44100 -ab 64 -ac 1 -acodec mp3 "movie.flv"
//                    "-ar 44100",
//                    "-ab 64",
//                    "-ac 1",
//                    "-acodec mp3",
//                    "output2.mp3"
//            );


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
