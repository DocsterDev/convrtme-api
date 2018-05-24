package com.convrt.executor;

import com.convrt.worker.YouTubeDownloadWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class YouTubeDownloadExecutor {

//    private final ExecutorService pool = Executors.newFixedThreadPool(5);

    public String execute(String videoId) {
        YouTubeDownloadWorker youTubeVideoDownloadWorker = new YouTubeDownloadWorker();
        youTubeVideoDownloadWorker.startDownload(videoId);
        String title = youTubeVideoDownloadWorker.getVideoInfo().getTitle();
        log.info("Downloading video with title is {}",title);
        return title;
    }

}
