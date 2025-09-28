package com.giasu.tutor_booking.dto.user;

import java.util.UUID;

public record ParentStudentLinkResponse(
        UUID parentProfileId,
        UUID studentProfileId,
        String studentName,
        String relationship,
        boolean primaryGuardian
) {
}
