package com.giasu.tutor_booking.dto.auth;

import java.time.Instant;

public record AuthResponse(
        String tokenType,
        String accessToken,
        Instant expiresAt,
        UserSummary user
) {
}