package com.convrt.api.service;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Video;
import com.convrt.api.repository.ChannelRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class VideoUploadEventService {
    @Autowired
    private ChannelRepository channelRepository;

    @Value("${subscriptionCallbackUrl}")
    private String subscriptionCallbackUrl;

    private static final String PUBSUBHUB_URL = "https://pubsubhubbub.appspot.com/subscribe";

    public void subscribe(String channelId) {
        callPubSubService(channelId, "subscribe");
    }

    public void unsubscribe(String channelId) {
        callPubSubService(channelId, "unsubscribe");
    }

    private void callPubSubService(String channelId, String action) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap();

        map.add("hub.callback", subscriptionCallbackUrl + "/api/video/event");
        map.add("hub.topic", String.format("https://www.youtube.com/xml/feeds/videos.xml?channel_id=%s", channelId));
        map.add("hub.mode", action);
        map.add("hub.verify", "async");
        map.add("hub.verify_token", channelId);
        map.add("hub.secret", StringUtils.EMPTY);
        map.add("hub.lease_seconds", StringUtils.EMPTY);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(map, headers);
        ResponseEntity<String> response = new RestTemplate().postForEntity(PUBSUBHUB_URL, request, String.class);
        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            log.info("Successfully {}d to/from channel ID {}", action, channelId);
            return;
        }
        throw new RuntimeException(String.format("Error %sing to channel id %s: Status code: %s", action, channelId, response.getStatusCodeValue()));
    }

    @Transactional
    public void updateOrAddVideo(String channelId, Video video) {
        Channel channel = channelRepository.findChannelByChannelId(channelId);
        if (channel == null) {
            throw new RuntimeException(String.format("No channel found for channel id %s", channelId));
        }
        List<Video> videoObjList = channel.getVideos();
        for (Video videoObj: videoObjList) {
            if (StringUtils.equals(videoObj.getId(), video.getId())) {
                videoObj.setSubscriptionScannedDate(Instant.now());
                return;
            }
        }
        video.setSubscriptionScannedDate(Instant.now());
        video.setChannel(channel);
        videoObjList.add(video);
    }
}
