package com.giasu.tutor_booking.dto.user;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ParentProfileCreateRequest(
        @NotNull UUID userAccountId,
        String displayName,
        String contactPhone,
        String preferredContactMethod,
        String notes
) {
}
