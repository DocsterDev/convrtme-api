package com.moup.api.utils;

import java.util.UUID;

public class UUIDUtils {

    public static String generateUuid(String input) {
        return UUID.nameUUIDFromBytes(input.getBytes()).toString();
    }

    public static String generateUuid(String... inputs) {
        StringBuilder sb = new StringBuilder();
        for(String input: inputs) {
            sb.append(input);
        }
        return UUID.nameUUIDFromBytes(sb.toString().getBytes()).toString();
    }

}
