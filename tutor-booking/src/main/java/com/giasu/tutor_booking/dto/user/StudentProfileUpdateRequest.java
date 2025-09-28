package com.giasu.tutor_booking.dto.user;

public record StudentProfileUpdateRequest(
        String gradeLevel,
        String learningGoals,
        String preferredLearningMode,
        String timeZone
) {
}
