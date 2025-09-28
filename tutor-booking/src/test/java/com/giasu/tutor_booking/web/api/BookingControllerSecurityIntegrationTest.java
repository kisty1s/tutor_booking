package com.giasu.tutor_booking.web.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giasu.tutor_booking.domain.booking.BookingStatus;
import com.giasu.tutor_booking.domain.subject.Subject;
import com.giasu.tutor_booking.domain.user.AccountStatus;
import com.giasu.tutor_booking.domain.user.ParentProfile;
import com.giasu.tutor_booking.domain.user.ParentStudentLink;
import com.giasu.tutor_booking.domain.user.StudentProfile;
import com.giasu.tutor_booking.domain.user.TutorProfile;
import com.giasu.tutor_booking.domain.user.UserAccount;
import com.giasu.tutor_booking.domain.user.UserRole;
import com.giasu.tutor_booking.dto.booking.BookingCreateRequest;
import com.giasu.tutor_booking.repository.ParentProfileRepository;
import com.giasu.tutor_booking.repository.ParentStudentLinkRepository;
import com.giasu.tutor_booking.repository.StudentProfileRepository;
import com.giasu.tutor_booking.repository.SubjectRepository;
import com.giasu.tutor_booking.repository.TutorProfileRepository;
import com.giasu.tutor_booking.repository.UserAccountRepository;
import com.giasu.tutor_booking.security.JwtService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingControllerSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private ParentProfileRepository parentProfileRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private ParentStudentLinkRepository parentStudentLinkRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private UserAccount unlinkedParentAccount;
    private UserAccount linkedParentAccount;
    private UserAccount studentAccount;
    private UserAccount tutorAccount;

    private ParentProfile linkedParentProfile;
    private ParentProfile unlinkedParentProfile;
    private StudentProfile studentProfile;
    private TutorProfile tutorProfile;
    private Subject subject;

    private String unlinkedParentToken;
    private String linkedParentToken;
    private String tutorToken;

    @BeforeEach
    void setUp() {
        subject = subjectRepository.save(Subject.builder()
                .code("SUB-" + UUID.randomUUID().toString().substring(0, 8))
                .name("Mathematics")
                .description("Mathematics subject")
                .active(true)
                .build());

        studentAccount = createAccount("student@test.com", UserRole.STUDENT);
        tutorAccount = createAccount("tutor@test.com", UserRole.TUTOR);
        unlinkedParentAccount = createAccount("parent@test.com", UserRole.PARENT);
        linkedParentAccount = createAccount("linked-parent@test.com", UserRole.PARENT);

        studentProfile = studentProfileRepository.save(StudentProfile.builder()
                .userAccount(studentAccount)
                .gradeLevel("Grade 8")
                .build());

        tutorProfile = tutorProfileRepository.save(TutorProfile.builder()
                .userAccount(tutorAccount)
                .headline("Experienced tutor")
                .verified(true)
                .build());

        unlinkedParentProfile = parentProfileRepository.save(ParentProfile.builder()
                .userAccount(unlinkedParentAccount)
                .displayName("Parent User")
                .build());

        linkedParentProfile = parentProfileRepository.save(ParentProfile.builder()
                .userAccount(linkedParentAccount)
                .displayName("Linked Parent")
                .build());

        parentStudentLinkRepository.save(ParentStudentLink.builder()
                .parent(linkedParentProfile)
                .student(studentProfile)
                .relationship("Mother")
                .primaryGuardian(true)
                .build());

        unlinkedParentToken = bearerToken(unlinkedParentAccount);
        linkedParentToken = bearerToken(linkedParentAccount);
        tutorToken = bearerToken(tutorAccount);
    }

    @Test
    void studentBookingsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/student/" + studentProfile.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tutorBookingsForbiddenForParent() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/tutor/" + tutorProfile.getId())
                        .header(HttpHeaders.AUTHORIZATION, unlinkedParentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void linkedParentCanViewStudentBookings() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/student/" + studentProfile.getId())
                        .header(HttpHeaders.AUTHORIZATION, linkedParentToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void linkedParentCanCreateBookingForStudent() throws Exception {
        createBookingAsLinkedParent();
    }

    @Test
    void unlinkedParentCannotCreateBooking() throws Exception {
        BookingCreateRequest request = buildCreateRequest(unlinkedParentProfile.getId());

        mockMvc.perform(post("/api/v1/bookings")
                        .header(HttpHeaders.AUTHORIZATION, unlinkedParentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void linkedParentCannotConfirmBooking() throws Exception {
        BookingResponseDto booking = createBookingAsLinkedParent();

        mockMvc.perform(post("/api/v1/bookings/" + booking.id() + "/confirm")
                        .header(HttpHeaders.AUTHORIZATION, linkedParentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void tutorCanConfirmBooking() throws Exception {
        BookingResponseDto booking = createBookingAsLinkedParent();

        mockMvc.perform(post("/api/v1/bookings/" + booking.id() + "/confirm")
                        .header(HttpHeaders.AUTHORIZATION, tutorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(BookingStatus.CONFIRMED.name()));

        mockMvc.perform(post("/api/v1/bookings/" + booking.id() + "/confirm")
                        .header(HttpHeaders.AUTHORIZATION, tutorToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void linkedParentCanCancelBooking() throws Exception {
        BookingResponseDto booking = createBookingAsLinkedParent();

        mockMvc.perform(post("/api/v1/bookings/" + booking.id() + "/cancel")
                        .header(HttpHeaders.AUTHORIZATION, linkedParentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(BookingStatus.CANCELLED.name()));
    }

    @Test
    void otherParentCannotCancelBooking() throws Exception {
        BookingResponseDto booking = createBookingAsLinkedParent();

        mockMvc.perform(post("/api/v1/bookings/" + booking.id() + "/cancel")
                        .header(HttpHeaders.AUTHORIZATION, unlinkedParentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void tutorCanCompleteConfirmedBooking() throws Exception {
        BookingResponseDto booking = createBookingAsLinkedParent();

        mockMvc.perform(post("/api/v1/bookings/" + booking.id() + "/confirm")
                        .header(HttpHeaders.AUTHORIZATION, tutorToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/bookings/" + booking.id() + "/complete")
                        .header(HttpHeaders.AUTHORIZATION, tutorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(BookingStatus.COMPLETED.name()));
    }

    @Test
    void completeBookingRequiresConfirmedStatus() throws Exception {
        BookingResponseDto booking = createBookingAsLinkedParent();

        mockMvc.perform(post("/api/v1/bookings/" + booking.id() + "/complete")
                        .header(HttpHeaders.AUTHORIZATION, tutorToken))
                .andExpect(status().isBadRequest());
    }

    private BookingResponseDto createBookingAsLinkedParent() throws Exception {
        return createBooking(linkedParentToken, linkedParentProfile.getId());
    }

    private BookingResponseDto createBooking(String token, UUID parentProfileId) throws Exception {
        BookingCreateRequest request = buildCreateRequest(parentProfileId);

        MvcResult result = mockMvc.perform(post("/api/v1/bookings")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), BookingResponseDto.class);
    }

    private BookingCreateRequest buildCreateRequest(UUID parentProfileId) {
        return new BookingCreateRequest(
                studentProfile.getId(),
                tutorProfile.getId(),
                subject.getId(),
                parentProfileId,
                OffsetDateTime.now().plusDays(1),
                60,
                BigDecimal.valueOf(450_000),
                "https://meet.example.com/" + UUID.randomUUID(),
                "Need focus on algebra"
        );
    }

    private UserAccount createAccount(String email, UserRole role) {
        UserAccount account = UserAccount.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123"))
                .fullName(role.name() + " User")
                .role(role)
                .status(AccountStatus.ACTIVE)
                .build();
        return userAccountRepository.save(account);
    }

    private String bearerToken(UserAccount account) {
        return "Bearer " + jwtService.generateToken(account);
    }

    private record BookingResponseDto(
            UUID id,
            UUID studentProfileId,
            UUID tutorProfileId,
            UUID subjectId,
            OffsetDateTime startTime,
            int durationMinutes,
            BigDecimal totalFee,
            BookingStatus status,
            String meetingLink,
            String notes
    ) {
    }
}