package com.giasu.tutor_booking.dto.user;

import java.util.UUID;

public record StudentLinkedParentResponse(
        UUID parentProfileId,
        String parentName,
        String relationship,
        boolean primaryGuardian
) {
}
