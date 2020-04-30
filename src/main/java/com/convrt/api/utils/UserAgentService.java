package com.convrt.api.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import ua_parser.Client;
import ua_parser.Parser;

import java.io.IOException;

@RequestScope
@Service
public class UserAgentService {

    private Client c;

    public void parseUserAgent(String userAgent) {
        Parser parser;
        try {
            parser = new Parser();
        } catch (IOException ioException) {
            throw new RuntimeException(String.format("Cannot read user agent string: %s", userAgent));
        }
        c = parser.parse(userAgent);
    }

    public String getBrowserFamily() {
        return (c != null) ? c.userAgent.family : "Unknown Browser";
    }

    public boolean isChrome() {
        if (c != null && c.userAgent.family.contains("Chrome")) {
            return true;
        }
        return false;
    }

}
