package com.giasu.tutor_booking.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.giasu.tutor_booking.domain.booking.Booking;
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
import com.giasu.tutor_booking.dto.booking.BookingResponse;
import com.giasu.tutor_booking.exception.BusinessValidationException;
import com.giasu.tutor_booking.exception.ResourceNotFoundException;
import com.giasu.tutor_booking.repository.BookingRepository;
import com.giasu.tutor_booking.repository.ParentProfileRepository;
import com.giasu.tutor_booking.repository.ParentStudentLinkRepository;
import com.giasu.tutor_booking.repository.StudentProfileRepository;
import com.giasu.tutor_booking.repository.SubjectRepository;
import com.giasu.tutor_booking.repository.TutorProfileRepository;
import com.giasu.tutor_booking.security.CurrentUserService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private TutorProfileRepository tutorProfileRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private ParentProfileRepository parentProfileRepository;

    @Mock
    private ParentStudentLinkRepository parentStudentLinkRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private UUID bookingId;
    private UUID studentId;
    private UUID tutorId;
    private UUID subjectId;
    private UUID parentId;
    private OffsetDateTime futureStart;

    private UserAccount adminUser;
    private UserAccount tutorUser;
    private UserAccount parentUser;
    private UserAccount studentUser;

    private StudentProfile studentProfile;
    private TutorProfile tutorProfile;
    private ParentProfile parentProfile;
    private Subject subject;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        tutorId = UUID.randomUUID();
        subjectId = UUID.randomUUID();
        parentId = UUID.randomUUID();
        futureStart = OffsetDateTime.now().plusDays(1);

        adminUser = buildUser(UserRole.ADMIN);
        tutorUser = buildUser(UserRole.TUTOR);
        parentUser = buildUser(UserRole.PARENT);
        studentUser = buildUser(UserRole.STUDENT);

        studentProfile = StudentProfile.builder().userAccount(studentUser).build();
        studentProfile.setId(studentId);

        tutorProfile = TutorProfile.builder().userAccount(tutorUser).build();
        tutorProfile.setId(tutorId);

        parentProfile = ParentProfile.builder().userAccount(parentUser).build();
        parentProfile.setId(parentId);

        subject = Subject.builder().build();
        subject.setId(subjectId);

        when(currentUserService.getCurrentUser()).thenReturn(adminUser);
        when(studentProfileRepository.findById(studentId)).thenReturn(Optional.of(studentProfile));
        when(tutorProfileRepository.findById(tutorId)).thenReturn(Optional.of(tutorProfile));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(parentProfileRepository.findById(parentId)).thenReturn(Optional.of(parentProfile));
        when(parentProfileRepository.findByUserAccountId(parentUser.getId())).thenReturn(Optional.of(parentProfile));
        when(parentStudentLinkRepository.findByParentIdAndStudentId(parentId, studentId))
                .thenReturn(Optional.of(ParentStudentLink.builder().build()));
    }

    @Test
    void createBooking_ShouldPersistWhenValid() {
        BookingCreateRequest request = new BookingCreateRequest(
                studentId,
                tutorId,
                subjectId,
                parentId,
                futureStart,
                60,
                new BigDecimal("450000.00"),
                " https://meet.example.com/abc ",
                "  Yeu cau tap trung vao mon toan  "
        );

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking toSave = invocation.getArgument(0);
            toSave.setId(UUID.randomUUID());
            return toSave;
        });

        BookingResponse response = bookingService.createBooking(request);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking persisted = bookingCaptor.getValue();

        assertThat(persisted.getStatus()).isEqualTo(BookingStatus.REQUESTED);
        assertThat(persisted.getMeetingLink()).isEqualTo("https://meet.example.com/abc");
        assertThat(persisted.getNotes()).isEqualTo("Yeu cau tap trung vao mon toan");

        assertThat(response.id()).isNotNull();
        assertThat(response.status()).isEqualTo(BookingStatus.REQUESTED);
        assertThat(response.studentProfileId()).isEqualTo(studentId);
        assertThat(response.tutorProfileId()).isEqualTo(tutorId);
    }

    @Test
    void createBooking_ShouldThrowWhenStudentMissing() {
        when(studentProfileRepository.findById(studentId)).thenReturn(Optional.empty());

        BookingCreateRequest request = new BookingCreateRequest(
                studentId,
                tutorId,
                subjectId,
                null,
                futureStart,
                60,
                new BigDecimal("150000.00"),
                null,
                null
        );

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_ShouldRejectPastStart() {
        BookingCreateRequest request = new BookingCreateRequest(
                studentId,
                tutorId,
                subjectId,
                null,
                OffsetDateTime.now().minusHours(2),
                45,
                new BigDecimal("120000.00"),
                null,
                null
        );

        assertThrows(BusinessValidationException.class, () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_ShouldRejectWhenParentNotLinked() {
        when(currentUserService.getCurrentUser()).thenReturn(parentUser);
        when(parentStudentLinkRepository.findByParentIdAndStudentId(parentId, studentId)).thenReturn(Optional.empty());

        BookingCreateRequest request = new BookingCreateRequest(
                studentId,
                tutorId,
                subjectId,
                parentId,
                futureStart,
                30,
                new BigDecimal("90000.00"),
                null,
                null
        );

        assertThrows(AccessDeniedException.class, () -> bookingService.createBooking(request));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void listBookingsForTutor_ShouldMapEntities() {
        when(bookingRepository.findByTutorIdOrderByStartTimeAsc(tutorId)).thenReturn(List.of(buildBooking(BookingStatus.CONFIRMED)));

        List<BookingResponse> responses = bookingService.listBookingsForTutor(tutorId);

        assertThat(responses).hasSize(1);
        BookingResponse response = responses.get(0);
        assertThat(response.tutorProfileId()).isEqualTo(tutorId);
        assertThat(response.status()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void listBookingsForTutor_ShouldRejectWhenUnauthorized() {
        when(currentUserService.getCurrentUser()).thenReturn(parentUser);

        assertThrows(AccessDeniedException.class, () -> bookingService.listBookingsForTutor(tutorId));
    }

    @Test
    void listBookingsForStudent_ShouldReturnEmptyWhenNone() {
        List<BookingResponse> responses = bookingService.listBookingsForStudent(studentId);

        assertThat(responses).isEmpty();
    }

    @Test
    void listBookingsForStudent_ShouldAllowParentWhenLinked() {
        when(currentUserService.getCurrentUser()).thenReturn(parentUser);
        when(bookingRepository.findByStudentIdOrderByStartTimeAsc(studentId)).thenReturn(List.of(buildBooking(BookingStatus.CONFIRMED)));

        List<BookingResponse> responses = bookingService.listBookingsForStudent(studentId);
        assertThat(responses).hasSize(1);
    }

    @Test
    void confirmBooking_ShouldAllowTutor() {
        when(currentUserService.getCurrentUser()).thenReturn(tutorUser);
        Booking booking = buildBooking(BookingStatus.REQUESTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService.confirmBooking(bookingId);

        assertThat(response.status()).isEqualTo(BookingStatus.CONFIRMED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void confirmBooking_ShouldRejectForInvalidStatus() {
        when(currentUserService.getCurrentUser()).thenReturn(tutorUser);
        Booking booking = buildBooking(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BusinessValidationException.class, () -> bookingService.confirmBooking(bookingId));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void confirmBooking_ShouldRejectForUnauthorizedUser() {
        when(currentUserService.getCurrentUser()).thenReturn(parentUser);
        Booking booking = buildBooking(BookingStatus.REQUESTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () -> bookingService.confirmBooking(bookingId));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelBooking_ShouldAllowParentWhenLinked() {
        when(currentUserService.getCurrentUser()).thenReturn(parentUser);
        Booking booking = buildBooking(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService.cancelBooking(bookingId);

        assertThat(response.status()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void cancelBooking_ShouldRejectWhenStatusNotCancellable() {
        when(currentUserService.getCurrentUser()).thenReturn(tutorUser);
        Booking booking = buildBooking(BookingStatus.COMPLETED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BusinessValidationException.class, () -> bookingService.cancelBooking(bookingId));
    }

    @Test
    void completeBooking_ShouldAllowTutor() {
        when(currentUserService.getCurrentUser()).thenReturn(tutorUser);
        Booking booking = buildBooking(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService.completeBooking(bookingId);

        assertThat(response.status()).isEqualTo(BookingStatus.COMPLETED);
    }

    @Test
    void completeBooking_ShouldRejectWhenNotConfirmed() {
        when(currentUserService.getCurrentUser()).thenReturn(tutorUser);
        Booking booking = buildBooking(BookingStatus.REQUESTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BusinessValidationException.class, () -> bookingService.completeBooking(bookingId));
    }

    private Booking buildBooking(BookingStatus status) {
        Booking booking = Booking.builder()
                .student(studentProfile)
                .tutor(tutorProfile)
                .subject(subject)
                .startTime(futureStart)
                .durationMinutes(60)
                .totalFee(new BigDecimal("200000.00"))
                .status(status)
                .meetingLink("https://meet.example.com/session")
                .notes("Note")
                .build();
        booking.setId(bookingId);
        return booking;
    }

    private UserAccount buildUser(UserRole role) {
        UserAccount account = UserAccount.builder()
                .email(role.name().toLowerCase() + "@example.com")
                .passwordHash("hashed")
                .fullName(role.name() + " User")
                .role(role)
                .status(AccountStatus.ACTIVE)
                .build();
        account.setId(UUID.randomUUID());
        return account;
    }
}