package com.localserve.localserve.controller;

import com.localserve.localserve.dto.*;
import com.localserve.localserve.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // USER + PROVIDER can create booking
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','PROVIDER')")
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return bookingService.createBooking(
                email,
                request.getServiceOfferingId(),
                request.getBookingTime()
        );
    }

    // USER + PROVIDER can view their own bookings
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER','PROVIDER')")
    public List<BookingResponse> getMyBookings() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return bookingService.getUserBookings(email);
    }

    // Only PROVIDER can view incoming bookings
    @GetMapping("/provider/my")
    @PreAuthorize("hasRole('PROVIDER')")
    public List<BookingResponse> getMyProviderBookings() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return bookingService.getProviderBookings(email);
    }

    // Only PROVIDER can accept
    @PutMapping("/{bookingId}/accept")
    @PreAuthorize("hasRole('PROVIDER')")
    public BookingResponse acceptBooking(@PathVariable Long bookingId) {
        return bookingService.acceptBooking(bookingId);
    }

    // Only PROVIDER can reject
    @PutMapping("/{bookingId}/reject")
    @PreAuthorize("hasRole('PROVIDER')")
    public BookingResponse rejectBooking(@PathVariable Long bookingId) {
        return bookingService.rejectBooking(bookingId);
    }

    // Only PROVIDER can complete
    @PutMapping("/{bookingId}/complete")
    @PreAuthorize("hasRole('PROVIDER')")
    public BookingResponse completeBooking(@PathVariable Long bookingId) {
        return bookingService.completeBooking(bookingId);
    }
}