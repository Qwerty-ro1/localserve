package com.localserve.localserve.service;

import com.localserve.localserve.dto.BookingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

public interface BookingService {


    BookingResponse acceptBooking(Long bookingId);

    BookingResponse rejectBooking(Long bookingId);

    BookingResponse completeBooking(Long bookingId);

    BookingResponse createBooking(String email, Long serviceOfferingId,
                                  LocalDateTime bookingTime, Long serviceAddressId);
    Page<BookingResponse> getUserBookings(String email, int page, int size, String sortBy, String direction);

    Page<BookingResponse> getProviderBookings(String email, int page, int size, String sortBy, String direction);
}