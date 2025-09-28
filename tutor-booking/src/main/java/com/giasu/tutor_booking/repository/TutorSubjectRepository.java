package com.giasu.tutor_booking.repository;

import com.giasu.tutor_booking.domain.subject.TeachingLevel;
import com.giasu.tutor_booking.domain.subject.TutorSubject;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorSubjectRepository extends JpaRepository<TutorSubject, UUID> {

    List<TutorSubject> findByTutorId(UUID tutorId);

    List<TutorSubject> findBySubjectIdAndTeachingLevel(UUID subjectId, TeachingLevel teachingLevel);
}
