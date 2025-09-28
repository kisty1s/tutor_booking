package com.giasu.tutor_booking.dto.booking;

import com.giasu.tutor_booking.domain.booking.BookingStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID studentProfileId,
        UUID tutorProfileId,
        UUID subjectId,
        OffsetDateTime startTime,
        int durationMinutes,
        BigDecimal totalFee,
        BookingStatus status,
        String meetingLink,
        String notes
) {
}