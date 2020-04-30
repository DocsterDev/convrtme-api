package com.convrt.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
public class URLUtils {

    public static boolean isAudioOnlyNope(String streamUrl) {
        boolean audioOnly = false;
        if (streamUrl != null) {
            MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(streamUrl).build().getQueryParams();
            List<String> mime = parameters.get("mime");
            if (!mime.isEmpty()) {
                String mimeStr = mime.get(0);
                if (StringUtils.isNotBlank(mimeStr)) {
                    audioOnly = mimeStr.contains("audio");
                    return audioOnly;
                }
            }
            log.info("Setting audio only: {} to mime type: {}", audioOnly, mime.get(0));
        }
        return audioOnly;
    }

}
