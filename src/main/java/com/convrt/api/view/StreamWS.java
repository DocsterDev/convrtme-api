package com.convrt.api.view;

import com.convrt.api.utils.URLUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StreamWS {

    public static final StreamWS ERROR = setError();

    private String id;
    private String title;
    private String owner;
    private String description;
    private long duration;
    private LocalDate uploadDate;
    private String extension;
    private String streamUrl;
    private boolean audioOnly;
    private boolean matchesExtension;
    private boolean success;
    private JsonNode data;

    public StreamWS(boolean success) {
        this.success = success;
    }

    private static StreamWS setError() {
        StreamWS streamWS = new StreamWS();
        streamWS.setSuccess(false);
        return streamWS;
    }
}
