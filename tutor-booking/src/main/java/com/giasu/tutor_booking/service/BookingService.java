package com.giasu.tutor_booking.service;

import com.giasu.tutor_booking.dto.booking.BookingCreateRequest;
import com.giasu.tutor_booking.dto.booking.BookingResponse;
import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingResponse createBooking(BookingCreateRequest request);

    List<BookingResponse> listBookingsForTutor(UUID tutorProfileId);

    List<BookingResponse> listBookingsForStudent(UUID studentProfileId);

    BookingResponse confirmBooking(UUID bookingId);

    BookingResponse cancelBooking(UUID bookingId);

    BookingResponse completeBooking(UUID bookingId);
}