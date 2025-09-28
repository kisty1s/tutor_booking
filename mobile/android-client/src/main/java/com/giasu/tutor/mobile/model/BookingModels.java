package com.giasu.tutor.mobile.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class BookingModels {

    private BookingModels() {
    }

    public record BookingCreateRequest(
            String studentProfileId,
            String tutorProfileId,
            String subjectId,
            String parentProfileId,
            OffsetDateTime startTime,
            int durationMinutes,
            BigDecimal totalFee,
            String meetingLink,
            String notes
    ) {
    }

    public record BookingResponse(
            String id,
            String studentProfileId,
            String tutorProfileId,
            String subjectId,
            OffsetDateTime startTime,
            int durationMinutes,
            BigDecimal totalFee,
            @SerializedName("status") String status,
            String meetingLink,
            String notes
    ) {
    }
}