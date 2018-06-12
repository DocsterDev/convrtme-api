package com.convrt.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class YouTubeConversionService {
    public void convertVideo() {

        final Path videoIn = Paths.get("test-output.webm");
        final Path encodingFile = Paths.get("output.webm");
        final Path errorFile = Paths.get("error.txt");

        int retCode;

        try {
            Files.deleteIfExists(encodingFile);
            Files.deleteIfExists(errorFile);

            // TODO Get correct converter 
            final ProcessBuilder pb
                    = new ProcessBuilder("ffmpeg.exe",
                    "-i", videoIn.toString(),
                    "-y",
                    "-s", "360x480", // stripped the extraneous "-1"
                    "-vcodec", "libx264",
                    "-"
            ); //or other command....

            pb.redirectError(errorFile.toFile());
            pb.redirectOutput(ProcessBuilder.Redirect.PIPE);

            final Process p = pb.start();

            // CHECK FOR THIS!
            retCode = p.waitFor();

            // Reproduced here; not sure this is anything useful,
            // since the old code, just like this one, just reads the contents
            // from the video file to be converted... Huh?
        } catch (Exception e) {
            // deal with e here
            e.printStackTrace();
        }

    }
}
