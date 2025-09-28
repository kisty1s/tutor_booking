package com.giasu.tutor_booking.service;

import com.giasu.tutor_booking.dto.user.ParentProfileCreateRequest;
import com.giasu.tutor_booking.dto.user.ParentProfileResponse;
import com.giasu.tutor_booking.dto.user.ParentProfileUpdateRequest;
import com.giasu.tutor_booking.dto.user.ParentStudentLinkCreateRequest;
import com.giasu.tutor_booking.dto.user.ParentStudentLinkResponse;
import java.util.List;
import java.util.UUID;

public interface ParentProfileService {

    ParentProfileResponse createParentProfile(ParentProfileCreateRequest request);

    ParentProfileResponse updateParentProfile(UUID parentId, ParentProfileUpdateRequest request);

    ParentProfileResponse getParentProfile(UUID parentId);

    List<ParentProfileResponse> listParentProfiles();

    void deleteParentProfile(UUID parentId);

    ParentStudentLinkResponse addStudentLink(UUID parentId, ParentStudentLinkCreateRequest request);

    void removeStudentLink(UUID parentId, UUID studentId);
}
