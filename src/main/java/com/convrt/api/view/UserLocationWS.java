package com.convrt.api.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLocationWS {
    @JsonProperty("query")
    private String ip;
    private String isp;
    private String city;
    private String region;
    private String regionName;
    private String country;
    private String countryCode;
    @JsonProperty("lat")
    private String latitude;
    @JsonProperty("lon")
    private String longitude;
    private String timezone;
    private String zip;
}
