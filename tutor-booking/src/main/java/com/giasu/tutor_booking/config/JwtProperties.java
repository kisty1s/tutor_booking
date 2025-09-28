package com.giasu.tutor_booking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.jwt")
public record JwtProperties(
        String secret,
        long expirationMinutes
) {
}
