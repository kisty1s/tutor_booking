package com.giasu.tutor_booking.repository;

import com.giasu.tutor_booking.domain.user.ParentStudentLink;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentStudentLinkRepository extends JpaRepository<ParentStudentLink, UUID> {

    List<ParentStudentLink> findByParentId(UUID parentId);

    List<ParentStudentLink> findByStudentId(UUID studentId);

    Optional<ParentStudentLink> findByParentIdAndStudentId(UUID parentId, UUID studentId);
}
