package com.giasu.tutor_booking.repository;

import com.giasu.tutor_booking.domain.user.TutorProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorProfileRepository extends JpaRepository<TutorProfile, UUID> {

    Optional<TutorProfile> findByUserAccountEmailIgnoreCase(String email);

    Optional<TutorProfile> findByUserAccountId(UUID userAccountId);
}