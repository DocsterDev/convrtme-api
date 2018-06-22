package com.convrt.utils;

import java.io.IOException;
import java.util.List;

import javax.persistence.AttributeConverter;

import com.convrt.view.VideoIdSet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JpaJsonConverter implements AttributeConverter<List<VideoIdSet>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<VideoIdSet> meta) {
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Override
    public List<VideoIdSet> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<VideoIdSet>>(){});
        } catch (IOException ex) {
            return null;
        }
    }
}