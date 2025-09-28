package com.giasu.tutor_booking.dto.user;

public record ParentProfileUpdateRequest(
        String displayName,
        String contactPhone,
        String preferredContactMethod,
        String notes
) {
}
