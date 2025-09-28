package com.giasu.tutor_booking.dto.subject;

import jakarta.validation.constraints.Size;

public record SubjectUpdateRequest(
        @Size(max = 30) String code,
        @Size(max = 120) String name,
        @Size(max = 500) String description,
        Boolean active
) {
}
