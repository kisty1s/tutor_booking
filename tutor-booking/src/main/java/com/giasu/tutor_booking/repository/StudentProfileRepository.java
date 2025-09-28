package com.giasu.tutor_booking.repository;

import com.giasu.tutor_booking.domain.user.StudentProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, UUID> {

    Optional<StudentProfile> findByUserAccountEmailIgnoreCase(String email);

    Optional<StudentProfile> findByUserAccountId(UUID userAccountId);
}