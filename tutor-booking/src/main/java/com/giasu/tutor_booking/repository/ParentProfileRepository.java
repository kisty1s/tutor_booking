package com.giasu.tutor_booking.repository;

import com.giasu.tutor_booking.domain.user.ParentProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentProfileRepository extends JpaRepository<ParentProfile, UUID> {

    Optional<ParentProfile> findByUserAccountEmailIgnoreCase(String email);

    Optional<ParentProfile> findByUserAccountId(UUID userAccountId);
}