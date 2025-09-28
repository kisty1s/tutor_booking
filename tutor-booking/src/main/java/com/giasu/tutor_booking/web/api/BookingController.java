package com.giasu.tutor_booking.web.api;

import com.giasu.tutor_booking.dto.booking.BookingCreateRequest;
import com.giasu.tutor_booking.dto.booking.BookingResponse;
import com.giasu.tutor_booking.service.BookingService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingCreateRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.created(URI.create("/api/v1/bookings/" + response.id())).body(response);
    }

    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingResponse> confirm(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(bookingService.confirmBooking(bookingId));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancel(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

    @PostMapping("/{bookingId}/complete")
    public ResponseEntity<BookingResponse> complete(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(bookingService.completeBooking(bookingId));
    }

    @GetMapping("/tutor/{tutorProfileId}")
    public ResponseEntity<List<BookingResponse>> listByTutor(@PathVariable UUID tutorProfileId) {
        return ResponseEntity.ok(bookingService.listBookingsForTutor(tutorProfileId));
    }

    @GetMapping("/student/{studentProfileId}")
    public ResponseEntity<List<BookingResponse>> listByStudent(@PathVariable UUID studentProfileId) {
        return ResponseEntity.ok(bookingService.listBookingsForStudent(studentProfileId));
    }
}