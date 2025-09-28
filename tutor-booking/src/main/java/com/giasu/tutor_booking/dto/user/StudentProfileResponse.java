package com.giasu.tutor_booking.dto.user;

import java.util.List;
import java.util.UUID;

public record StudentProfileResponse(
        UUID id,
        UUID userAccountId,
        String email,
        String gradeLevel,
        String learningGoals,
        String preferredLearningMode,
        String timeZone,
        List<StudentLinkedParentResponse> parents
) {
}
