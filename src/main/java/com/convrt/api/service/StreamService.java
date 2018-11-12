package com.convrt.api.service;

import com.convrt.api.entity.Channel;
import com.convrt.api.entity.Stream;
import com.convrt.api.entity.Video;
import com.convrt.api.repository.ChannelRepository;
import com.convrt.api.repository.StreamRepository;
import com.convrt.api.utils.UUIDUtils;
import com.convrt.api.view.StreamWS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@Service
public class StreamService {
    @Autowired
    private StreamRepository streamRepository;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private AudioExtractorService audioExtractorService;
    @Autowired
    private ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd");

    @Transactional
    public Stream readStream(String videoId, String extension) {
        return streamRepository.findByVideoIdAndExtension(videoId, extension);
    }

    /*
    @Transactional
    public Stream createOrUpdate(Video video, String extension, String streamUrl) {
        Stream streamPersistent = readStream(video.getId(), extension);

        streamPersistent.setVideo(video);
        streamPersistent.setExtension(extension);
        streamPersistent.setStreamUrl(streamUrl);
        return streamRepository.save(streamPersistent);
    }
    */

    public StreamWS fetchStreamUrl(String videoId, String extension, String userAgent) {
       Stream streamPersistent = readStream(videoId, extension);
        //Stream streamPersistent = null;
        Video videoPersistent = null;
        if (streamPersistent != null) {
            videoPersistent = videoService.readVideoByVideoId(videoId);
            streamPersistent = videoPersistent.getStreams().get(extension);
        }
        StreamWS gblStreamWS = null;
        if (Objects.isNull(streamPersistent) || streamPersistent.getStreamUrl() == null || Instant.now().isAfter(streamPersistent.getStreamUrlExpireDate())) {
            gblStreamWS = getYoutubeDLStream(videoId, extension, userAgent);
            if (!gblStreamWS.isSuccess()) {
                return StreamWS.ERROR;
            }

            if (Objects.isNull(videoPersistent)) {
                videoPersistent = new Video();
                videoPersistent.setId(gblStreamWS.getId());
                videoPersistent.setTitle(gblStreamWS.getTitle());
                Channel channel = channelService.createChannel(gblStreamWS.getOwner());
                videoPersistent.setChannel(channel);
                //videoPersistent.setDescription(gblStreamWS.getDescription());
                videoPersistent.setDurationSec(gblStreamWS.getDuration());
                videoPersistent.setUploadDate(gblStreamWS.getUploadDate());
            }

            streamPersistent = new Stream();
            streamPersistent.setUuid(UUIDUtils.generateUuid(gblStreamWS.getId(), gblStreamWS.getExtension()));
            String extn = gblStreamWS.getExtension();
            streamPersistent.setExtension(extn);
            streamPersistent.setStreamUrl(gblStreamWS.getStreamUrl());
            streamPersistent.setAudioOnly(gblStreamWS.isAudioOnly());
            streamPersistent.setMatchesExtension(gblStreamWS.isMatchesExtension());
            videoPersistent.getStreams().put(extension, streamPersistent);
            videoService.createOrUpdateVideo(videoPersistent);
        }
        StreamWS streamWS = new StreamWS();
        streamWS.setId(videoPersistent.getId());
        streamWS.setTitle(videoPersistent.getTitle());
        streamWS.setOwner(videoPersistent.getChannel().getName());
        streamWS.setDescription(videoPersistent.getDescription());
        streamWS.setDuration(videoPersistent.getDurationSec());
        streamWS.setUploadDate(videoPersistent.getUploadDate());
        streamWS.setExtension(streamPersistent.getExtension());
        streamWS.setStreamUrl(streamPersistent.getStreamUrl());
        streamWS.setAudioOnly(streamPersistent.isAudioOnly());
        streamWS.setData(gblStreamWS.getData());
        streamWS.setMatchesExtension(streamPersistent.isMatchesExtension());
        streamWS.setSuccess(StringUtils.isNotBlank(streamPersistent.getStreamUrl()));
        return streamWS;
    }

    public StreamWS getYoutubeDLStream(String videoId, String extension, String userAgent){
        ProcessBuilder pb = audioExtractorService.buildProcess(videoId, extension, userAgent);
        try {
            Process p = pb.start();
            try (InputStream is = p.getInputStream(); InputStream es = p.getErrorStream()) {
                String error = IOUtils.toString(es, "UTF-8");
                String output = IOUtils.toString(is, "UTF-8");
                if (StringUtils.isBlank(output) || StringUtils.isNotBlank(error)) {
                    log.error("Extracted stream URL is null for video id {}: {}", videoId, error);
                    return StreamWS.ERROR;
                }
                return parseVideoInfo(output, extension);
            } catch (IOException e) {
                log.error("Error executing YouTube-DL to extract video id for {}", videoId, e);
                return StreamWS.ERROR;
            }
        } catch (Exception e) {
            log.error("Error starting process to extract url for video id {}", videoId, e);
            return StreamWS.ERROR;
        }
    }


    private StreamWS parseVideoInfo(String videoInfoJson, String extension) {
        try {
            if (StringUtils.isNotBlank(videoInfoJson)) {
                JsonNode videoInfo = objectMapper.readTree(videoInfoJson);
                StreamWS streamWS = new StreamWS();
                streamWS.setData(videoInfo);
                streamWS.setId(videoInfo.get("id").asText());
                streamWS.setTitle(videoInfo.get("title").asText());
                streamWS.setDuration(videoInfo.get("duration").asLong());
                streamWS.setOwner(videoInfo.get("uploader").asText());
                streamWS.setDescription(videoInfo.get("description").asText());
                String uploadDateStr = videoInfo.get("upload_date").asText();
                LocalDate uploadDate = LocalDate.parse(uploadDateStr, DATE_FORMATTER);
                streamWS.setUploadDate(uploadDate);
                String ext = videoInfo.get("ext").asText();
                streamWS.setExtension(ext);
                streamWS.setMatchesExtension(StringUtils.equalsIgnoreCase(extension, ext));
                String streamUrl = videoInfo.get("url").asText();
                streamWS.setStreamUrl(streamUrl);
                String format = videoInfo.get("format_note").asText();
                streamWS.setAudioOnly(StringUtils.contains(format, "DASH audio"));
                streamWS.setSuccess(true);
                return streamWS;
            }
            return StreamWS.ERROR;
        } catch (Exception e) {
            log.info("Error parsing video json extract", e);
            return StreamWS.ERROR;
        }
    }
}
