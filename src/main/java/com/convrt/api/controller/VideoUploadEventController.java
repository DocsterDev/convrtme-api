package com.convrt.api.controller;

import com.convrt.api.entity.Video;
import com.convrt.api.service.ChannelService;
import com.convrt.api.service.VideoUploadEventService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Iterator;

@Slf4j
@RestController
@RequestMapping("/api/video")
public class VideoUploadEventController {
    private static final ObjectMapper XML_MAPPER = new XmlMapper();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    private VideoUploadEventService videoUploadEventService;
    @Autowired
    private ChannelService channelService;

    @PostMapping("/channel/{channelId}/subscribe")
    public void subscribe(@PathVariable("channelId") String channelId) {
        videoUploadEventService.subscribe(channelId);
    }

    @PostMapping("/channel/{channelId}/unsubscribe")
    public void unsubscribe(@PathVariable("channelId") String channelId) {
        videoUploadEventService.unsubscribe(channelId);
    }

    @GetMapping("/event")
    public String verifyEndpoint(
            @RequestParam("hub.topic") String topic,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam(value = "hub.verify_token", required = false) String verifyToken,
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.lease_seconds") String leaseSeconds) {
        log.info("Challenge:  {}", challenge);
        log.info("Channel Id: {}", verifyToken);
        log.info("Mode:       {}", mode);
        channelService.updateSubscribed(verifyToken, mode);
        return challenge;
    }

    @PostMapping(value = "/event", consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
    public void receiveUploadVideoEvent(@RequestBody String body) {
        String channelId;
        String videoId;
        String title;
        String owner;
        String publishedDate;
        try {
            ObjectNode object = XML_MAPPER.readValue(body, ObjectNode.class);
            JsonNode entry = object.get("entry");
            channelId = entry.get("channelId").asText();
            videoId = entry.get("videoId").asText();
            title = entry.get("title").asText();
            owner = entry.get("author").get("name").asText();
            publishedDate = entry.get("published").asText();

            Video video = new Video();
            video.setId(videoId);
            video.setTitle(title);
            video.setOwner(owner);

            TemporalAccessor accessor = DATE_TIME_FORMATTER.parse(publishedDate);
            Instant publishedDateTime = Instant.from(accessor);
            video.setUploadDate(publishedDateTime);
            videoUploadEventService.updateOrAddVideo(channelId, video);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to parse xml body: %s", body), e);
        }
    }

}
