package com.moup.api.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamFormatWS {

    @JsonProperty("format_note")
    private String formatNote;

    @JsonProperty("url")
    private String url;

    @JsonProperty("abr")
    private float abr;

    @JsonProperty("tbr")
    private float tbr;

    @JsonProperty("format")
    private String format;

    @JsonProperty("filesize")
    private long fileSize;

    @JsonProperty("acodec")
    private String audioCodec;

    @JsonProperty("ext")
    private String extension;

    @JsonProperty("format_id")
    private String formatId;

    private boolean audioOnly;

    public void setFormat(String format) {
        this.format = format;
        this.audioOnly = StringUtils.contains(format, "audio only");
    }

}
