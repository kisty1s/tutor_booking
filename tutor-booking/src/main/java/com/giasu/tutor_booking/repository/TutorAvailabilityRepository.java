package com.giasu.tutor_booking.repository;

import com.giasu.tutor_booking.domain.user.TutorAvailability;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorAvailabilityRepository extends JpaRepository<TutorAvailability, UUID> {

    List<TutorAvailability> findByTutorIdAndDayOfWeek(UUID tutorId, DayOfWeek dayOfWeek);
}
