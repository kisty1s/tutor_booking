package com.giasu.tutor_booking.service.impl;

import com.giasu.tutor_booking.domain.user.ParentProfile;
import com.giasu.tutor_booking.domain.user.ParentStudentLink;
import com.giasu.tutor_booking.domain.user.StudentProfile;
import com.giasu.tutor_booking.domain.user.UserAccount;
import com.giasu.tutor_booking.dto.user.ParentProfileCreateRequest;
import com.giasu.tutor_booking.dto.user.ParentProfileResponse;
import com.giasu.tutor_booking.dto.user.ParentProfileUpdateRequest;
import com.giasu.tutor_booking.dto.user.ParentStudentLinkCreateRequest;
import com.giasu.tutor_booking.dto.user.ParentStudentLinkResponse;
import com.giasu.tutor_booking.dto.user.StudentLinkedParentResponse;
import com.giasu.tutor_booking.dto.user.StudentProfileCreateRequest;
import com.giasu.tutor_booking.dto.user.StudentProfileResponse;
import com.giasu.tutor_booking.dto.user.StudentProfileUpdateRequest;
import com.giasu.tutor_booking.exception.ResourceAlreadyExistsException;
import com.giasu.tutor_booking.exception.ResourceNotFoundException;
import com.giasu.tutor_booking.repository.ParentProfileRepository;
import com.giasu.tutor_booking.repository.ParentStudentLinkRepository;
import com.giasu.tutor_booking.repository.StudentProfileRepository;
import com.giasu.tutor_booking.repository.UserAccountRepository;
import com.giasu.tutor_booking.service.ParentProfileService;
import com.giasu.tutor_booking.service.StudentProfileService;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ParentProfileServiceImpl implements ParentProfileService, StudentProfileService {

    private final ParentProfileRepository parentProfileRepository;
    private final ParentStudentLinkRepository parentStudentLinkRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public ParentProfileResponse createParentProfile(ParentProfileCreateRequest request) {
        UserAccount userAccount = userAccountRepository.findById(request.userAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.userAccountId()));
        if (userAccount.getParentProfile() != null) {
            throw new ResourceAlreadyExistsException("Parent profile already exists for user: " + userAccount.getEmail());
        }

        ParentProfile parentProfile = ParentProfile.builder()
                .userAccount(userAccount)
                .displayName(request.displayName())
                .contactPhone(request.contactPhone())
                .preferredContactMethod(request.preferredContactMethod())
                .notes(request.notes())
                .build();

        ParentProfile saved = parentProfileRepository.save(parentProfile);
        userAccount.setParentProfile(saved);
        return toParentResponse(saved);
    }

    @Override
    public ParentProfileResponse updateParentProfile(UUID parentId, ParentProfileUpdateRequest request) {
        ParentProfile parentProfile = parentProfileRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent profile not found: " + parentId));

        if (request.displayName() != null) {
            parentProfile.setDisplayName(request.displayName());
        }
        if (request.contactPhone() != null) {
            parentProfile.setContactPhone(request.contactPhone());
        }
        if (request.preferredContactMethod() != null) {
            parentProfile.setPreferredContactMethod(request.preferredContactMethod());
        }
        if (request.notes() != null) {
            parentProfile.setNotes(request.notes());
        }

        return toParentResponse(parentProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public ParentProfileResponse getParentProfile(UUID parentId) {
        ParentProfile parentProfile = parentProfileRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent profile not found: " + parentId));
        return toParentResponse(parentProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParentProfileResponse> listParentProfiles() {
        return parentProfileRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt")).stream()
                .map(this::toParentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteParentProfile(UUID parentId) {
        ParentProfile parentProfile = parentProfileRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent profile not found: " + parentId));
        UserAccount userAccount = parentProfile.getUserAccount();
        if (userAccount != null) {
            userAccount.setParentProfile(null);
        }
        parentProfileRepository.delete(parentProfile);
    }

    @Override
    public ParentStudentLinkResponse addStudentLink(UUID parentId, ParentStudentLinkCreateRequest request) {
        ParentProfile parentProfile = parentProfileRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent profile not found: " + parentId));
        StudentProfile studentProfile = studentProfileRepository.findById(request.studentProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found: " + request.studentProfileId()));

        boolean alreadyLinked = parentProfile.getStudentLinks().stream()
                .anyMatch(link -> link.getStudent().getId().equals(studentProfile.getId()));
        if (alreadyLinked) {
            throw new ResourceAlreadyExistsException("Student already linked to parent");
        }

        ParentStudentLink link = ParentStudentLink.builder()
                .parent(parentProfile)
                .student(studentProfile)
                .relationship(request.relationship())
                .primaryGuardian(Boolean.TRUE.equals(request.primaryGuardian()))
                .build();

        if (link.isPrimaryGuardian()) {
            parentProfile.getStudentLinks().forEach(existing -> existing.setPrimaryGuardian(false));
        }

        parentProfile.getStudentLinks().add(link);
        studentProfile.getParentLinks().add(link);

        ParentStudentLink saved = parentStudentLinkRepository.save(link);
        return toStudentLinkResponse(saved);
    }

    @Override
    public void removeStudentLink(UUID parentId, UUID studentId) {
        ParentStudentLink link = parentStudentLinkRepository.findByParentIdAndStudentId(parentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent/student association not found"));

        ParentProfile parent = link.getParent();
        StudentProfile student = link.getStudent();
        parent.getStudentLinks().remove(link);
        student.getParentLinks().remove(link);
        parentStudentLinkRepository.delete(link);
    }

    private ParentProfileResponse toParentResponse(ParentProfile parentProfile) {
        List<ParentStudentLinkResponse> students = parentProfile.getStudentLinks().stream()
                .sorted(Comparator.comparing(link -> link.getStudent().getUserAccount().getFullName(), String.CASE_INSENSITIVE_ORDER))
                .map(this::toStudentLinkResponse)
                .collect(Collectors.toList());

        UserAccount userAccount = parentProfile.getUserAccount();
        return new ParentProfileResponse(
                parentProfile.getId(),
                userAccount.getId(),
                userAccount.getEmail(),
                parentProfile.getDisplayName(),
                parentProfile.getContactPhone(),
                parentProfile.getPreferredContactMethod(),
                parentProfile.getNotes(),
                students
        );
    }

    private ParentStudentLinkResponse toStudentLinkResponse(ParentStudentLink link) {
        StudentProfile student = link.getStudent();
        UserAccount studentAccount = student.getUserAccount();
        return new ParentStudentLinkResponse(
                link.getParent().getId(),
                student.getId(),
                studentAccount.getFullName(),
                link.getRelationship(),
                link.isPrimaryGuardian()
        );
    }

    // StudentProfileService delegation methods

    @Override
    public StudentProfileResponse createStudentProfile(StudentProfileCreateRequest request) {
        UserAccount userAccount = userAccountRepository.findById(request.userAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.userAccountId()));
        if (userAccount.getStudentProfile() != null) {
            throw new ResourceAlreadyExistsException("Student profile already exists for user: " + userAccount.getEmail());
        }

        StudentProfile studentProfile = StudentProfile.builder()
                .userAccount(userAccount)
                .gradeLevel(request.gradeLevel())
                .learningGoals(request.learningGoals())
                .preferredLearningMode(request.preferredLearningMode())
                .timeZone(request.timeZone())
                .build();

        StudentProfile saved = studentProfileRepository.save(studentProfile);
        userAccount.setStudentProfile(saved);
        return toStudentResponse(saved);
    }

    @Override
    public StudentProfileResponse updateStudentProfile(UUID studentId, StudentProfileUpdateRequest request) {
        StudentProfile studentProfile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found: " + studentId));

        if (request.gradeLevel() != null) {
            studentProfile.setGradeLevel(request.gradeLevel());
        }
        if (request.learningGoals() != null) {
            studentProfile.setLearningGoals(request.learningGoals());
        }
        if (request.preferredLearningMode() != null) {
            studentProfile.setPreferredLearningMode(request.preferredLearningMode());
        }
        if (request.timeZone() != null) {
            studentProfile.setTimeZone(request.timeZone());
        }

        return toStudentResponse(studentProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(UUID studentId) {
        StudentProfile studentProfile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found: " + studentId));
        return toStudentResponse(studentProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfileResponse> listStudentProfiles() {
        return studentProfileRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt")).stream()
                .map(this::toStudentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteStudentProfile(UUID studentId) {
        StudentProfile studentProfile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found: " + studentId));
        UserAccount userAccount = studentProfile.getUserAccount();
        if (userAccount != null) {
            userAccount.setStudentProfile(null);
        }
        studentProfileRepository.delete(studentProfile);
    }

    private StudentProfileResponse toStudentResponse(StudentProfile studentProfile) {
        List<StudentLinkedParentResponse> parents = studentProfile.getParentLinks().stream()
                .sorted(Comparator.comparing(link -> link.getParent().getUserAccount().getFullName(), String.CASE_INSENSITIVE_ORDER))
                .map(link -> new StudentLinkedParentResponse(
                        link.getParent().getId(),
                        link.getParent().getUserAccount().getFullName(),
                        link.getRelationship(),
                        link.isPrimaryGuardian()
                ))
                .collect(Collectors.toList());

        UserAccount account = studentProfile.getUserAccount();
        return new StudentProfileResponse(
                studentProfile.getId(),
                account.getId(),
                account.getEmail(),
                studentProfile.getGradeLevel(),
                studentProfile.getLearningGoals(),
                studentProfile.getPreferredLearningMode(),
                studentProfile.getTimeZone(),
                parents
        );
    }
}








