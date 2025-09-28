package com.giasu.tutor_booking.dto.booking;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingCreateRequest(
        @NotNull UUID studentProfileId,
        @NotNull UUID tutorProfileId,
        @NotNull UUID subjectId,
        UUID parentProfileId,
        @NotNull OffsetDateTime startTime,
        @Min(15) int durationMinutes,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal totalFee,
        @Size(max = 255) String meetingLink,
        @Size(max = 500) String notes
) {
}