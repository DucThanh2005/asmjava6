package com.example.carstore.util;

import java.util.Map;

public final class ResponseUtils {

    private ResponseUtils() {
    }

    public static Map<String, Object> ok(String message) {
        return Map.of("success", true, "message", message);
    }

    public static Map<String, Object> ok(String key, Object value) {
        return Map.of("success", true, key, value);
    }

    public static Map<String, Object> ok(String message, String key, Object value) {
        return Map.of("success", true, "message", message, key, value);
    }

    public static Map<String, Object> fail(String message) {
        return Map.of("success", false, "message", message);
    }
}
