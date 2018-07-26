package com.convrt.api.utils;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

public class UUIDUtils {

    public static String generateUuid(String input) {
        List<String> inputs = Lists.newArrayList(input);
        return generateUuid(input);
    }

    public static String generateUuid(String... inputs) {
        StringBuilder sb = new StringBuilder();
        for(String input: inputs) {
            sb.append(input);
        }
        return UUID.nameUUIDFromBytes(sb.toString().getBytes()).toString();
    }

}
