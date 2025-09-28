package com.giasu.tutor_booking.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.giasu.tutor_booking.domain.user.AccountStatus;
import com.giasu.tutor_booking.domain.user.ParentProfile;
import com.giasu.tutor_booking.domain.user.ParentStudentLink;
import com.giasu.tutor_booking.domain.user.StudentProfile;
import com.giasu.tutor_booking.domain.user.UserAccount;
import com.giasu.tutor_booking.domain.user.UserRole;
import com.giasu.tutor_booking.dto.user.ParentProfileCreateRequest;
import com.giasu.tutor_booking.dto.user.ParentStudentLinkCreateRequest;
import com.giasu.tutor_booking.dto.user.StudentProfileCreateRequest;
import com.giasu.tutor_booking.exception.ResourceAlreadyExistsException;
import com.giasu.tutor_booking.exception.ResourceNotFoundException;
import com.giasu.tutor_booking.repository.ParentProfileRepository;
import com.giasu.tutor_booking.repository.ParentStudentLinkRepository;
import com.giasu.tutor_booking.repository.StudentProfileRepository;
import com.giasu.tutor_booking.repository.UserAccountRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParentProfileServiceImplTest {

    @Mock
    private ParentProfileRepository parentProfileRepository;

    @Mock
    private ParentStudentLinkRepository parentStudentLinkRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private ParentProfileServiceImpl parentProfileService;

    private UserAccount parentAccount;
    private UserAccount studentAccount;

    @BeforeEach
    void setUp() {
        parentAccount = buildUser("parent@example.com", UserRole.PARENT);
        studentAccount = buildUser("student@example.com", UserRole.STUDENT);
    }

    @Test
    void createParentProfile_ShouldCreateNewProfile() {
        ParentProfileCreateRequest request = new ParentProfileCreateRequest(parentAccount.getId(), "Ph? huynh A", "0900000000", "PHONE", "Ghi ch?");

        when(userAccountRepository.findById(parentAccount.getId())).thenReturn(Optional.of(parentAccount));
        when(parentProfileRepository.save(any(ParentProfile.class))).thenAnswer(invocation -> {
            ParentProfile profile = invocation.getArgument(0);
            profile.setId(UUID.randomUUID());
            return profile;
        });

        var response = parentProfileService.createParentProfile(request);

        assertThat(response.email()).isEqualTo(parentAccount.getEmail());
        assertThat(response.displayName()).isEqualTo("Ph? huynh A");
        verify(parentProfileRepository).save(any(ParentProfile.class));
        assertThat(parentAccount.getParentProfile()).isNotNull();
    }

    @Test
    void createParentProfile_ShouldThrowWhenAlreadyExists() {
        ParentProfile existing = ParentProfile.builder().userAccount(parentAccount).studentLinks(new HashSet<>()).build();
        parentAccount.setParentProfile(existing);

        when(userAccountRepository.findById(parentAccount.getId())).thenReturn(Optional.of(parentAccount));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> parentProfileService.createParentProfile(new ParentProfileCreateRequest(parentAccount.getId(), null, null, null, null)));
    }

    @Test
    void addStudentLink_ShouldLinkParentAndStudent() {
        UUID parentId = parentAccount.getId();
        UUID studentId = studentAccount.getId();

        ParentProfile parentProfile = ParentProfile.builder()
                .userAccount(parentAccount)
                .studentLinks(new HashSet<>())
                .build();
        parentProfile.setId(parentId);

        StudentProfile studentProfile = StudentProfile.builder()
                .userAccount(studentAccount)
                .parentLinks(new HashSet<>())
                .build();
        studentProfile.setId(studentId);

        when(parentProfileRepository.findById(parentId)).thenReturn(Optional.of(parentProfile));
        when(studentProfileRepository.findById(studentId)).thenReturn(Optional.of(studentProfile));
        when(parentStudentLinkRepository.save(any(ParentStudentLink.class))).thenAnswer(invocation -> {
            ParentStudentLink link = invocation.getArgument(0);
            link.setId(UUID.randomUUID());
            return link;
        });

        var response = parentProfileService.addStudentLink(parentId,
                new ParentStudentLinkCreateRequest(studentId, "M?", true));

        assertThat(response.studentProfileId()).isEqualTo(studentId);
        assertThat(response.primaryGuardian()).isTrue();
        assertThat(parentProfile.getStudentLinks()).hasSize(1);
        assertThat(studentProfile.getParentLinks()).hasSize(1);
    }

    @Test
    void createStudentProfile_ShouldAssignToUser() {
        StudentProfileCreateRequest request = new StudentProfileCreateRequest(studentAccount.getId(), "L?p 8", "?n thi", "ONLINE", "Asia/Ho_Chi_Minh");

        when(userAccountRepository.findById(studentAccount.getId())).thenReturn(Optional.of(studentAccount));
        when(studentProfileRepository.save(any(StudentProfile.class))).thenAnswer(invocation -> {
            StudentProfile studentProfile = invocation.getArgument(0);
            studentProfile.setId(UUID.randomUUID());
            return studentProfile;
        });

        var response = parentProfileService.createStudentProfile(request);

        assertThat(response.email()).isEqualTo(studentAccount.getEmail());
        assertThat(studentAccount.getStudentProfile()).isNotNull();
    }

    @Test
    void createStudentProfile_ShouldThrowWhenExisting() {
        StudentProfile existing = StudentProfile.builder().userAccount(studentAccount).parentLinks(new HashSet<>()).build();
        studentAccount.setStudentProfile(existing);

        when(userAccountRepository.findById(studentAccount.getId())).thenReturn(Optional.of(studentAccount));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> parentProfileService.createStudentProfile(new StudentProfileCreateRequest(studentAccount.getId(), null, null, null, null)));
    }

    @Test
    void addStudentLink_ShouldThrowWhenParentMissing() {
        UUID parentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        when(parentProfileRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                parentProfileService.addStudentLink(parentId, new ParentStudentLinkCreateRequest(studentId, null, null)));
    }

    private UserAccount buildUser(String email, UserRole role) {
        UserAccount account = UserAccount.builder()
                .email(email)
                .passwordHash("hash")
                .fullName("Test User")
                .phoneNumber(null)
                .role(role)
                .status(AccountStatus.ACTIVE)
                .build();
        account.setId(UUID.randomUUID());
        account.setCreatedAt(java.time.Instant.now());
        account.setUpdatedAt(account.getCreatedAt());
        return account;
    }
}
