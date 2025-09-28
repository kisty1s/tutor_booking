package com.giasu.tutor_booking.service;

import com.giasu.tutor_booking.dto.user.StudentProfileCreateRequest;
import com.giasu.tutor_booking.dto.user.StudentProfileResponse;
import com.giasu.tutor_booking.dto.user.StudentProfileUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface StudentProfileService {

    StudentProfileResponse createStudentProfile(StudentProfileCreateRequest request);

    StudentProfileResponse updateStudentProfile(UUID studentId, StudentProfileUpdateRequest request);

    StudentProfileResponse getStudentProfile(UUID studentId);

    List<StudentProfileResponse> listStudentProfiles();

    void deleteStudentProfile(UUID studentId);
}
