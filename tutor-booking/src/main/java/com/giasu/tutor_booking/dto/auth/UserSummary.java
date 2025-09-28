package com.giasu.tutor_booking.dto.auth;

import com.giasu.tutor_booking.domain.user.AccountStatus;
import com.giasu.tutor_booking.domain.user.UserRole;
import java.util.UUID;

public record UserSummary(
        UUID id,
        String email,
        String fullName,
        String phoneNumber,
        UserRole role,
        AccountStatus status
) {
}