package com.giasu.tutor_booking.repository;

import com.giasu.tutor_booking.domain.review.Review;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByTutorIdAndVisibleTrue(UUID tutorId);
}
