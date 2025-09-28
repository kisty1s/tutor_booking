package com.giasu.tutor_booking.dto.subject;

import java.util.UUID;

public record SubjectResponse(
        UUID id,
        String code,
        String name,
        String description,
        boolean active
) {
}
