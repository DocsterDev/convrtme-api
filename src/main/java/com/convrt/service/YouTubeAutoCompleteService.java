package com.convrt.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

@Slf4j
@Service
public class YouTubeAutoCompleteService {

    @Cacheable("autocomplete")
    public JsonNode getAutoCompleteLookup(String input) {
        log.info("Retrieving autocomplete lookup for input \"{}\"", input);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("suggestqueries.google.com")
                .path("/complete/search")
                .queryParam("client", "firefox")
                .queryParam("ds", "yt")
                .queryParam("q", input)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.ALL, MediaType.parseMediaType("text/javascript")));
        restTemplate.getMessageConverters().add(converter);
        return restTemplate.getForObject(uriComponents.toUriString(), JsonNode.class);
    }

}
