package com.convrt.service;

import com.convrt.entity.PlayCount;
import com.convrt.entity.User;
import com.convrt.entity.Video;
import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class StreamMetadataService {

    @Autowired
    private VideoService videoService;
    @Autowired
    private PlayCountService playCountService;
    @Autowired
    private UserService userService;

    static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=%s";

    @Transactional
    public Video mapStreamData(Video video, String userUuid) {
        String videoId = video.getId();
        log.info("Attempting to fetch existing valid stream url for video={}", videoId);
        Video videoPersistent = videoService.readVideoMetadata(videoId);
        if (videoPersistent == null) {
            log.info("No existing stream url available for video={}", videoId);
            String streamUrl = getStreamUrl(videoId);
            if (StringUtils.isNotBlank(streamUrl)){
                MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(streamUrl).build().getQueryParams();
                List<String> param1 = parameters.get("expire");
                video.setStreamUrlExpireDate(Instant.ofEpochSecond(Long.valueOf(param1.get(0))));
                video.setStreamUrl(streamUrl);
                video.setStreamUrlDate(Instant.now());
            }
            videoService.createVideo(video);
        }
        if (userUuid != null) {
            User user = userService.readUser(userUuid);
            PlayCount playCount = user.iteratePlayCount(video, user);
            userService.updateUser(user);
        }
        return video;
    }

    private String getStreamUrl(String videoId) {
        try {
            final AtomicBoolean stop = new AtomicBoolean(false);
            URL web = new URL(String.format(YOUTUBE_URL, videoId));
            VGetParser user = VGet.parser(web);
            VideoInfo videoinfo = user.info(web);
            new VGet(videoinfo).extract(user, stop, () -> {});
            List<VideoFileInfo> list = videoinfo.getInfo();
            return findAudioStreamUrl(list);
        } catch (NullPointerException e) {
            throw new RuntimeException("Sorry Bro, looks like we couldn't find this video!", e);
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
