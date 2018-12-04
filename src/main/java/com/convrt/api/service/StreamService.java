package com.convrt.api.service;

import com.convrt.api.entity.*;
//import com.convrt.api.entity.Stream;
import com.convrt.api.repository.ChannelRepository;
import com.convrt.api.repository.StreamRepository;
import com.convrt.api.repository.UserVideoRepository;
import com.convrt.api.utils.UUIDUtils;
import com.convrt.api.view.StreamFormatWS;
import com.convrt.api.view.StreamWS;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private ContextService contextService;
    @Autowired
    private UserVideoRepository userVideoRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd");

//    @Transactional
//    public Stream readStream(String videoId, String extension) {
//        return streamRepository.findByVideoIdAndExtension(videoId, extension);
//    }

    public StreamWS fetchStreamUrl(String videoId, boolean isChrome, String token) {
        log.info("Fetching video url for id {}", videoId);
        StreamWS streamWS = getYoutubeDLStream(videoId, isChrome, token);
        return streamWS;
    }

    /*
    public StreamWS fetchStreamUrl(String videoId, boolean isChrome) {
       String extension = isChrome ? "webm" : "m4a";
       Stream streamPersistent = readStream(videoId, extension);
        Video videoPersistent = null;
        if (streamPersistent != null) {
            videoPersistent = videoService.readVideoByVideoId(videoId);
            streamPersistent = videoPersistent.getStreams().get(extension);
        }
        StreamWS streamExtract;
        if (Objects.isNull(streamPersistent) || streamPersistent.getStreamUrl() == null || Instant.now().isAfter(streamPersistent.getStreamUrlExpireDate())) {
            streamExtract = getYoutubeDLStream(videoId, isChrome);

            if (Objects.isNull(videoPersistent)) {
                videoPersistent = new Video();
                videoPersistent.setId(streamExtract.getId());
                videoPersistent.setTitle(streamExtract.getTitle());
                Channel channel = channelService.createChannel(streamExtract.getOwner());
                videoPersistent.setChannel(channel);
                videoPersistent.setDurationSec(streamExtract.getDuration());
                videoPersistent.setUploadDate(streamExtract.getUploadDate());
            }

            streamPersistent = new Stream();
            StreamFormatWS streamFormatWS = streamExtract.getRecommendedFormat();
            streamPersistent.setUuid(UUIDUtils.generateUuid(streamExtract.getId(), streamFormatWS.getExtension()));
            streamPersistent.setExtension(streamFormatWS.getExtension());
            streamPersistent.setStreamUrl(streamFormatWS.getUrl());
            streamPersistent.setAudioOnly(streamFormatWS.isAudioOnly());
            streamPersistent.setAbr(streamFormatWS.getAbr());
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
        // streamWS.setData(gblStreamWS.getData());
        streamWS.setMatchesExtension(streamPersistent.isMatchesExtension());
        streamWS.setSuccess(StringUtils.isNotBlank(streamPersistent.getStreamUrl()));
        return streamWS;
    }
    */

    public StreamWS getYoutubeDLStream(String videoId, boolean isChrome, String token){
        ProcessBuilder pb = audioExtractorService.buildProcess(videoId, isChrome);
        try {
            Process p = pb.start();
            try (InputStream is = p.getInputStream(); InputStream es = p.getErrorStream()) {
                String error = IOUtils.toString(es, "UTF-8");
                String output = IOUtils.toString(is, "UTF-8");
                if (StringUtils.isBlank(output) || StringUtils.isNotBlank(error)) {
                    throw new RuntimeException(String.format("No stream found for video %s", videoId));
                }
                return parseVideoInfo(output, isChrome, token);
            } catch (IOException e) {
                throw new RuntimeException(String.format("No stream found for video %s", videoId));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public StreamWS parseVideoInfo(String videoInfoJson, boolean isChrome, String token) {
        try {
            JsonNode videoInfo = objectMapper.readTree(videoInfoJson);
            StreamWS streamWS = new StreamWS();
            String videoId = videoInfo.get("id").asText();
            streamWS.setId(videoId);
            streamWS.setTitle(videoInfo.get("title").asText());
            streamWS.setDuration(videoInfo.get("duration").asLong());
            streamWS.setOwner(videoInfo.get("uploader").asText());
            streamWS.setDescription(videoInfo.get("description").asText());
            String uploadDateStr = videoInfo.get("upload_date").asText();
            LocalDate uploadDate = LocalDate.parse(uploadDateStr, DATE_FORMATTER);
            streamWS.setUploadDate(uploadDate);
            streamWS.setChrome(isChrome);
            if (StringUtils.isNotBlank(token)) {
                User user = contextService.validateUserByTokenNoCheck(token);
                if (user != null) {
                    UserVideo userVideo = userVideoRepository.findFirstByUserUuidAndVideoIdOrderByVideosOrderDesc(user.getUuid(), videoId);
                    if (userVideo != null) {
                        Long elapsed = userVideo.getPlayheadPosition();
                        streamWS.setWatchedTime(elapsed);
                        // userVideo.setPlayheadPosition(null); // TODO Remove if you want to reset after seek load
                    }
                }
            }

            JsonNode formatsNode = videoInfo.get("formats");
            List<StreamFormatWS> formats = objectMapper.convertValue(formatsNode, new TypeReference<List<StreamFormatWS>>(){});

            Optional<StreamFormatWS> filteredAndAbrSorted = formats.stream()
                    .filter(e ->
                         !isChrome && StringUtils.equals(e.getExtension(), "webm") ? false : true
                    )
                    .sorted(Comparator.comparing(StreamFormatWS::isAudioOnly)
                                    .reversed()
                            .thenComparing(Comparator.comparing(StreamFormatWS::getAbr)
                                            .reversed()))
                    .findFirst();
            if (filteredAndAbrSorted.isPresent()) {
                // log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(filteredAndAbrSorted.get()));
                streamWS.setRecommendedFormat(filteredAndAbrSorted.get());
            }
            // streamWS.setFormats(formats);
            return streamWS;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
