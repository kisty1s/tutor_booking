package com.giasu.tutor_booking.repository;

import com.giasu.tutor_booking.domain.booking.Booking;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByTutorIdOrderByStartTimeAsc(UUID tutorProfileId);

    List<Booking> findByStudentIdOrderByStartTimeAsc(UUID studentProfileId);
}