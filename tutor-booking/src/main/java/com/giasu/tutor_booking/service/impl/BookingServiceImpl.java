package com.giasu.tutor_booking.service.impl;

import com.giasu.tutor_booking.domain.booking.Booking;
import com.giasu.tutor_booking.domain.booking.BookingStatus;
import com.giasu.tutor_booking.domain.subject.Subject;
import com.giasu.tutor_booking.domain.user.ParentProfile;
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
import com.giasu.tutor_booking.service.BookingService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Set<BookingStatus> CANCELLABLE_STATUSES = Set.of(BookingStatus.REQUESTED, BookingStatus.CONFIRMED);

    private final BookingRepository bookingRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final TutorProfileRepository tutorProfileRepository;
    private final SubjectRepository subjectRepository;
    private final ParentProfileRepository parentProfileRepository;
    private final ParentStudentLinkRepository parentStudentLinkRepository;
    private final CurrentUserService currentUserService;

    @Override
    public BookingResponse createBooking(BookingCreateRequest request) {
        validateBusinessRules(request);

        StudentProfile student = studentProfileRepository.findById(request.studentProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found: " + request.studentProfileId()));

        TutorProfile tutor = tutorProfileRepository.findById(request.tutorProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor profile not found: " + request.tutorProfileId()));

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + request.subjectId()));

        UserAccount currentUser = currentUserService.getCurrentUser();
        ParentProfile parent = resolveParentForCreation(currentUser, student, request.parentProfileId());

        Booking booking = Booking.builder()
                .student(student)
                .tutor(tutor)
                .subject(subject)
                .startTime(request.startTime())
                .durationMinutes(request.durationMinutes())
                .totalFee(request.totalFee())
                .status(BookingStatus.REQUESTED)
                .meetingLink(trimToNull(request.meetingLink()))
                .notes(trimToNull(request.notes()))
                .build();

        return toResponse(bookingRepository.save(booking));
    }

    private void validateBusinessRules(BookingCreateRequest request) {
        if (request.durationMinutes() <= 0) {
            throw new BusinessValidationException("Duration must be greater than zero");
        }
        if (request.totalFee().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Total fee must be greater than zero");
        }
        OffsetDateTime startTime = request.startTime();
        if (startTime.isBefore(OffsetDateTime.now().minusMinutes(1))) {
            throw new BusinessValidationException("Start time must be in the future");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> listBookingsForTutor(UUID tutorProfileId) {
        TutorProfile tutor = tutorProfileRepository.findById(tutorProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor profile not found: " + tutorProfileId));

        requireTutorAccess(currentUserService.getCurrentUser(), tutor);

        return bookingRepository.findByTutorIdOrderByStartTimeAsc(tutorProfileId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> listBookingsForStudent(UUID studentProfileId) {
        StudentProfile student = studentProfileRepository.findById(studentProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found: " + studentProfileId));

        requireStudentAccess(currentUserService.getCurrentUser(), student);

        return bookingRepository.findByStudentIdOrderByStartTimeAsc(studentProfileId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse confirmBooking(UUID bookingId) {
        Booking booking = getBooking(bookingId);
        requireTutorAccess(currentUserService.getCurrentUser(), booking.getTutor());
        if (booking.getStatus() != BookingStatus.REQUESTED) {
            throw new BusinessValidationException("Booking cannot be confirmed in its current state");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        return toResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse cancelBooking(UUID bookingId) {
        Booking booking = getBooking(bookingId);
        requireCancelPermission(currentUserService.getCurrentUser(), booking);
        if (!CANCELLABLE_STATUSES.contains(booking.getStatus())) {
            throw new BusinessValidationException("Booking cannot be cancelled in its current state");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse completeBooking(UUID bookingId) {
        Booking booking = getBooking(bookingId);
        requireTutorAccess(currentUserService.getCurrentUser(), booking.getTutor());
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessValidationException("Only confirmed bookings can be completed");
        }
        booking.setStatus(BookingStatus.COMPLETED);
        return toResponse(bookingRepository.save(booking));
    }

    private Booking getBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
    }

    private ParentProfile resolveParentForCreation(UserAccount user, StudentProfile student, UUID requestedParentId) {
        if (isAdmin(user)) {
            if (requestedParentId == null) {
                return null;
            }
            ParentProfile parent = parentProfileRepository.findById(requestedParentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent profile not found: " + requestedParentId));
            ensureParentLinkedToStudent(parent.getId(), student.getId());
            return parent;
        }

        if (user.getRole() == UserRole.PARENT) {
            ParentProfile parent = requireParentProfile(user);
            assertParentLinkedForAccess(parent.getId(), student.getId());
            return parent;
        }

        if (user.getRole() == UserRole.STUDENT) {
            requireStudentOwner(user, student);
            if (requestedParentId != null) {
                ParentProfile parent = parentProfileRepository.findById(requestedParentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Parent profile not found: " + requestedParentId));
                ensureParentLinkedToStudent(parent.getId(), student.getId());
                return parent;
            }
            return null;
        }

        throw new AccessDeniedException("You are not allowed to create bookings");
    }

    private void requireTutorAccess(UserAccount user, TutorProfile tutor) {
        if (isAdmin(user)) {
            return;
        }
        if (user.getRole() == UserRole.TUTOR) {
            if (tutor.getUserAccount() == null || !tutor.getUserAccount().getId().equals(user.getId())) {
                throw new AccessDeniedException("You do not have permission to manage this booking");
            }
            return;
        }
        throw new AccessDeniedException("You do not have permission to manage this booking");
    }

    private void requireStudentAccess(UserAccount user, StudentProfile student) {
        if (isAdmin(user)) {
            return;
        }
        if (user.getRole() == UserRole.STUDENT) {
            requireStudentOwner(user, student);
            return;
        }
        if (user.getRole() == UserRole.PARENT) {
            ParentProfile parent = requireParentProfile(user);
            assertParentLinkedForAccess(parent.getId(), student.getId());
            return;
        }
        throw new AccessDeniedException("You do not have permission to access this student's bookings");
    }

    private void requireCancelPermission(UserAccount user, Booking booking) {
        if (isAdmin(user)) {
            return;
        }
        if (user.getRole() == UserRole.TUTOR) {
            requireTutorAccess(user, booking.getTutor());
            return;
        }
        if (user.getRole() == UserRole.STUDENT) {
            requireStudentOwner(user, booking.getStudent());
            return;
        }
        if (user.getRole() == UserRole.PARENT) {
            ParentProfile parent = requireParentProfile(user);
            assertParentLinkedForAccess(parent.getId(), booking.getStudent().getId());
            return;
        }
        throw new AccessDeniedException("You do not have permission to cancel this booking");
    }

    private ParentProfile requireParentProfile(UserAccount user) {
        return parentProfileRepository.findByUserAccountId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("Parent profile not found for user"));
    }

    private void requireStudentOwner(UserAccount user, StudentProfile student) {
        if (student.getUserAccount() == null || !student.getUserAccount().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to act on this student");
        }
    }

    private void ensureParentLinkedToStudent(UUID parentId, UUID studentId) {
        boolean linked = parentStudentLinkRepository.findByParentIdAndStudentId(parentId, studentId).isPresent();
        if (!linked) {
            throw new BusinessValidationException("Parent profile is not linked to the specified student");
        }
    }

    private void assertParentLinkedForAccess(UUID parentId, UUID studentId) {
        boolean linked = parentStudentLinkRepository.findByParentIdAndStudentId(parentId, studentId).isPresent();
        if (!linked) {
            throw new AccessDeniedException("You do not have permission to access this student's bookings");
        }
    }

    private boolean isAdmin(UserAccount user) {
        return user.getRole() == UserRole.ADMIN;
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStudent().getId(),
                booking.getTutor().getId(),
                booking.getSubject().getId(),
                booking.getStartTime(),
                booking.getDurationMinutes(),
                booking.getTotalFee(),
                booking.getStatus(),
                booking.getMeetingLink(),
                booking.getNotes()
        );
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}