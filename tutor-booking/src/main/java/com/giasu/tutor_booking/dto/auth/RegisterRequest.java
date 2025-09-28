package com.giasu.tutor_booking.dto.auth;

import com.giasu.tutor_booking.domain.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Email
        @Size(max = 180)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotBlank
        @Size(max = 120)
        String fullName,

        @Size(max = 30)
        String phoneNumber,

        UserRole role
) {
}