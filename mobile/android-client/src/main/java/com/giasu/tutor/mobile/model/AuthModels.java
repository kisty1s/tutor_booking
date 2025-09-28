package com.giasu.tutor.mobile.model;

import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import java.util.Objects;

public final class AuthModels {

    private AuthModels() {
    }

    public record LoginRequest(String email, String password) {
    }

    public record RegisterRequest(String email, String password, String fullName, String phoneNumber, String role) {
    }

    public static RegisterRequest parent(String email, String password, String fullName, String phoneNumber) {
        return new RegisterRequest(email, password, fullName, phoneNumber, "PARENT");
    }

    public record UserSummary(
            String id,
            String email,
            @SerializedName("fullName") String fullName,
            String phoneNumber,
            String role,
            String status
    ) {
    }

    public record AuthResponse(
            String tokenType,
            String accessToken,
            Instant expiresAt,
            UserSummary user
    ) {
    }
}