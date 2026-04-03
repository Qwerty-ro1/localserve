package com.localserve.localserve.controller;

import com.localserve.localserve.dto.*;
import com.localserve.localserve.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // USER + PROVIDER can create booking
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','PROVIDER')")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody CreateBookingRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        BookingResponse response = bookingService.createBooking(
                email,
                request.getServiceOfferingId(),
                request.getBookingTime()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking request sent successfully", response));
    }

    // USER + PROVIDER can view their own bookings
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER','PROVIDER')")
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> getUserBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookingTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Page<BookingResponse> response = bookingService.getUserBookings(email, page, size, sortBy, direction);

        return ResponseEntity.ok(ApiResponse.success("User bookings retrieved", response));
    }

    // Only PROVIDER can view incoming bookings
    @GetMapping("/provider/my")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> getProviderBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookingTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Page<BookingResponse> response = bookingService.getProviderBookings(email, page, size, sortBy, direction);

        return ResponseEntity.ok(ApiResponse.success("Provider incoming bookings retrieved", response));
    }

    // Only PROVIDER can accept
    @PutMapping("/{bookingId}/accept")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<BookingResponse>> acceptBooking(@PathVariable Long bookingId) {
        BookingResponse response = bookingService.acceptBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Booking accepted", response));
    }

    // Only PROVIDER can reject
    @PutMapping("/{bookingId}/reject")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<BookingResponse>> rejectBooking(@PathVariable Long bookingId) {
        BookingResponse response = bookingService.rejectBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Booking rejected", response));
    }

    // Only PROVIDER can complete
    @PutMapping("/{bookingId}/complete")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<BookingResponse>> completeBooking(@PathVariable Long bookingId) {
        BookingResponse response = bookingService.completeBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Booking marked as completed", response));
    }
}