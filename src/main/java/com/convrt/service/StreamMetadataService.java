package com.convrt.service;

import com.convrt.entity.Context;
import com.convrt.entity.User;
import com.convrt.view.VideoStreamMetadata;
import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class StreamMetadataService {

    @Autowired
    private VideoService videoService;
    @Autowired
    private PlayCountService playCountService;

    static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=%s";

    private VideoStreamMetadata startDownload(String videoId) {
        try {
            final AtomicBoolean stop = new AtomicBoolean(false);
            URL web = new URL(String.format(YOUTUBE_URL, videoId));
            VGetParser user = VGet.parser(web);
            VideoInfo videoinfo = user.info(web);
            new VGet(videoinfo).extract(user, stop, () -> {});
            List<VideoFileInfo> list = videoinfo.getInfo();
            return getMediaStreamUrl(list);
        } catch (NullPointerException e) {
            throw new RuntimeException("Sorry Bro, looks like we couldn't find this video!", e);
        } catch (Exception e) {
            throw new RuntimeException("Oops, looks like something went wrong :(", e);
        }
    }

    private VideoStreamMetadata getMediaStreamUrl(List<VideoFileInfo> list) {
        VideoFileInfo videoFileInfo = null;
        if (list != null) {
            for (VideoFileInfo d : list) {
                log.info("Found content-type: " + d.getContentType());
                if (d.getContentType().contains("audio")) {
                    log.info("Dedicated audio url found");
                    return new VideoStreamMetadata(d.getSource().toString(), d.getLength(), d.getContentType(), true);
                }
                videoFileInfo = d;
            }
            log.info("No dedicated audio url found. Returning full video url.");
            return new VideoStreamMetadata(videoFileInfo.getSource().toString(), videoFileInfo.getLength(), videoFileInfo.getContentType(), false);
        }
        throw new RuntimeException("Could not extract media stream url.");
    }

    @Transactional
    public VideoStreamMetadata mapStreamData(VideoStreamMetadata videoStreamMetadata) {
        String videoId = videoStreamMetadata.getVideoId();
        log.info("Attempting to fetch existing valid stream url for video={}", videoId);
        VideoStreamMetadata persistentVideoMetadata = videoService.readVideoByVideoId(videoId);
        if (persistentVideoMetadata == null) {
            log.info("No existing stream url available for video={}", videoId);
            persistentVideoMetadata = startDownload(videoId);
            videoStreamMetadata.setSource(persistentVideoMetadata.getSource());
            videoStreamMetadata.setLength(persistentVideoMetadata.getLength());
            videoStreamMetadata.setContentType(persistentVideoMetadata.getContentType());
            videoStreamMetadata.setAudio(persistentVideoMetadata.isAudio());
            videoStreamMetadata.setSourceFetchedDate(persistentVideoMetadata.getSourceFetchedDate());
            videoStreamMetadata.setSourceExpireDate(persistentVideoMetadata.getSourceExpireDate());
            videoService.createVideo(videoStreamMetadata);
        }
        videoStreamMetadata.setPlayCount(playCountService.iterateNumPlays(videoId));
        return videoStreamMetadata;
    }

}
