package com.giasu.tutor.mobile;

public final class ApiConfig {
    private ApiConfig() {
    }

    private static String baseUrl = "http://10.0.2.2:8080"; // Android emulator to localhost

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void overrideBaseUrl(String url) {
        baseUrl = url;
    }
}