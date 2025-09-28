package com.giasu.tutor_booking.dto.subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectCreateRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 500) String description,
        Boolean active
) {
}
