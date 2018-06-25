package com.convrt.entity;

import com.convrt.enums.ActionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "log", indexes = @Index(name = "log_ip_address_idx0", columnList = "ip_address"))
public class Log {

    @Id
    @Column(name = "uuid", length = 36)
    private String uuid;

    @JsonProperty("geoplugin_request")
    @Column(name = "ip_address", length = 30)
    private String ipAddress;

    @JsonProperty("geoplugin_continentName")
    @Column(name = "continent", length = 50)
    private String continent;

    @JsonProperty("geoplugin_continentCode")
    @Column(name = "continent_code", length = 30)
    private String continentCode;

    @JsonProperty("geoplugin_countryName")
    @Column(name = "country", length = 30)
    private String country;

    @JsonProperty("geoplugin_countryCode")
    @Column(name = "country_code", length = 10)
    private String countryCode;

    @JsonProperty("geoplugin_city")
    @Column(name = "city", length = 30)
    private String city;

    @JsonProperty("geoplugin_region")
    @Column(name = "region", length = 50)
    private String region;

    @JsonProperty("geoplugin_regionCode")
    @Column(name = "region_code", length = 10)
    private String regionCode;

    @JsonProperty("geoplugin_latitude")
    @Column(name = "latitude", length = 50)
    private String latitude;

    @JsonProperty("geoplugin_longitude")
    @Column(name = "longitude", length = 30)
    private String longitude;

    @JsonProperty("geoplugin_timezone")
    @Column(name = "timezone", length = 30)
    private String timezone;

    @Getter
    @Column(name = "date_accessed")
    private Instant dateAccessed;

    @NonNull
    @Column(name = "action", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType action;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "context_uuid", foreignKey = @ForeignKey(name = "fk_context_log_context_uuid"))
    private Context context;

}
