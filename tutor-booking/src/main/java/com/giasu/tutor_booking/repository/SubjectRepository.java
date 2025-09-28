package com.giasu.tutor_booking.repository;

import com.giasu.tutor_booking.domain.subject.Subject;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    Optional<Subject> findByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);
}
