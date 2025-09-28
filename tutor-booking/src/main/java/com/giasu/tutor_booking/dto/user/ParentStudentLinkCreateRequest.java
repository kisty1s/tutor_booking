package com.giasu.tutor_booking.dto.user;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ParentStudentLinkCreateRequest(
        @NotNull UUID studentProfileId,
        String relationship,
        Boolean primaryGuardian
) {
}
