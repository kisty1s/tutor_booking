package com.giasu.tutor_booking.dto.user;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StudentProfileCreateRequest(
        @NotNull UUID userAccountId,
        String gradeLevel,
        String learningGoals,
        String preferredLearningMode,
        String timeZone
) {
}
