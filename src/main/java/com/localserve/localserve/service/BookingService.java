package com.localserve.localserve.service;

import com.localserve.localserve.dto.BookingResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {


    BookingResponse acceptBooking(Long bookingId);

    BookingResponse rejectBooking(Long bookingId);

    BookingResponse completeBooking(Long bookingId);

    BookingResponse createBooking(String email, Long serviceOfferingId, LocalDateTime bookingTime);

    List<BookingResponse> getUserBookings(String email);

    List<BookingResponse> getProviderBookings(String email);
}