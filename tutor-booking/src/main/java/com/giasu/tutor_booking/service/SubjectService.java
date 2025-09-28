package com.giasu.tutor_booking.service;

import com.giasu.tutor_booking.dto.subject.SubjectCreateRequest;
import com.giasu.tutor_booking.dto.subject.SubjectResponse;
import com.giasu.tutor_booking.dto.subject.SubjectUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface SubjectService {

    SubjectResponse createSubject(SubjectCreateRequest request);

    List<SubjectResponse> listSubjects();

    SubjectResponse updateSubject(UUID subjectId, SubjectUpdateRequest request);

    void deleteSubject(UUID subjectId);
}
