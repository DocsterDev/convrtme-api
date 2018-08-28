package com.convrt.api.service;

import com.convrt.api.entity.Video;
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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class StreamMetadataService {

    @Autowired
    private VideoService videoService;

    static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=%s";

    @Transactional
    public Video updateVideoMetadata(Video video) {
        assert video.getTitle() != null;
        assert video.getOwner() != null;
        assert video.getDuration() != null;
        Video videoPersistent = videoService.readVideoByVideoId(video.getId());
        if (videoPersistent == null) {
            videoPersistent = new Video();
            videoPersistent.setId(UUID.randomUUID().toString());
        }
        videoPersistent.setTitle(videoPersistent.getTitle() != null ? videoPersistent.getTitle() : video.getTitle());
        videoPersistent.setOwner(videoPersistent.getOwner() != null ? videoPersistent.getOwner() : video.getOwner());
        videoPersistent.setDuration(videoPersistent.getDuration() != null ? videoPersistent.getDuration() : video.getDuration());
        return videoService.createOrUpdateVideo(video);
    }

    @Transactional
    public Video fetchStreamUrl(String videoId) {
        if (videoId == null) {
            throw new RuntimeException("Cannot retrieve streamUrl when videoId is null.");
        }
        String streamUrl = getStreamUrl(videoId);
        log.info("Video stream URL found: {}", streamUrl);
        Video video = videoService.readVideoMetadata(videoId);
        if (video == null) {
            video = new Video();
            video.setId(videoId);
            video.setStreamUrl(streamUrl);
            return videoService.createOrUpdateVideo(video);
        }
        video.setStreamUrl(streamUrl);
        return video;
    }

    private Video mapStreamData(Video video) {
        String videoId = video.getId();

        Video videoPersistent = videoService.readVideoMetadata(videoId);
        if (videoPersistent == null) {
            videoPersistent = video;
            log.info("No existing stream url available for video={}", videoId);
            String streamUrl = getStreamUrl(videoId);
//            if (StringUtils.isNotBlank(streamUrl)){
//                MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(streamUrl).build().getQueryParams();
//                List<String> param1 = parameters.get("expire");
//                videoPersistent.setStreamUrlExpireDate(Instant.ofEpochSecond(Long.valueOf(param1.get(0))));
//                videoPersistent.setStreamUrl(streamUrl);
//                videoPersistent.setStreamUrlDate(Instant.now());
//            }
            //videoService.createVideo(videoPersistent);
        }

        return videoPersistent;
    }

    private String getStreamUrl(String videoId) {
        log.info("Attempting to fetch existing valid stream url for video={}", videoId);
        try {
            final AtomicBoolean stop = new AtomicBoolean(false);
            URL web = new URL(String.format(YOUTUBE_URL, videoId));
            VGetParser user = VGet.parser(web);
            VideoInfo videoinfo = user.info(web);
            new VGet(videoinfo).extract(user, stop, () -> {
            });
            List<VideoFileInfo> list = videoinfo.getInfo();
            return findAudioStreamUrl(list);
        } catch (NullPointerException e) {
            throw new RuntimeException("Sorry bruh, looks like we couldn't find this video!", e);
        } catch (Exception e) {
            throw new RuntimeException("Oops, looks like something went wrong :(", e);
        }
    }

    private String findAudioStreamUrl(List<VideoFileInfo> list) {
        VideoFileInfo videoFileInfo = null;
        if (list != null) {
            for (VideoFileInfo d : list) {
                log.info("Found content-type: " + d.getContentType());
                if (d.getContentType().contains("audio")) {
                    log.info("Dedicated audio url found");
                    return d.getSource().toString();
                }
                videoFileInfo = d;
            }
            log.info("No dedicated audio url found. Returning full video url.");
            return videoFileInfo.getSource().toString();
        }
        throw new RuntimeException("Could not extract media stream url.");
    }

}
