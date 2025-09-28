package com.giasu.tutor_booking.dto.user;

import java.util.List;
import java.util.UUID;

public record ParentProfileResponse(
        UUID id,
        UUID userAccountId,
        String email,
        String displayName,
        String contactPhone,
        String preferredContactMethod,
        String notes,
        List<ParentStudentLinkResponse> students
) {
}
