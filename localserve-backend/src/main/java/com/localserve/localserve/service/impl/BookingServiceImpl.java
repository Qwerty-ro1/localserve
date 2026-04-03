package com.localserve.localserve.service.impl;

import com.localserve.localserve.dto.BookingResponse;
import com.localserve.localserve.entity.*;
import com.localserve.localserve.exception.BadRequestException;
import com.localserve.localserve.exception.ResourceNotFoundException;
import com.localserve.localserve.exception.UnauthorizedException;
import com.localserve.localserve.repository.*;
import com.localserve.localserve.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;

    @Override
    public BookingResponse createBooking(String email, Long serviceOfferingId, LocalDateTime bookingTime) {

        // Fetch user making the booking
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Fetch the service being booked
        ServiceOffering serviceOffering = serviceOfferingRepository.findById(serviceOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Service offering not found"));

        Provider provider = serviceOffering.getProvider();

        // Prevent users from booking their own services
        if (provider.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot book your own service");
        }

        // Create new booking with initial status REQUESTED
        Booking booking = Booking.builder()
                .user(user)
                .provider(provider)
                .serviceOffering(serviceOffering)
                .status(BookingStatus.REQUESTED)
                .bookingTime(bookingTime)
                .build();

        // Save and return mapped response
        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public Page<BookingResponse> getUserBookings(String email, int page, int size, String sortBy, String direction) {

        // Fetch logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Whitelist sorting fields
        if (!List.of("bookingTime", "status").contains(sortBy)) {
            sortBy = "bookingTime";
        }

        // Build sort
        Sort sort = Sort.by(sortBy);

        if (direction.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        // Create pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch from DB with pagination + sorting
        Page<Booking> bookings = bookingRepository.findByUser(user, pageable);

        // Map to DTO
        return bookings.map(this::mapToResponse);
    }

    @Override
    public Page<BookingResponse> getProviderBookings(String email, int page, int size, String sortBy, String direction){

        // Fetch logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Fetch provider linked to this user
        Provider provider = providerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        // Whitelist allowed sorting fields
        if (!List.of("bookingTime", "status").contains(sortBy)) {
            sortBy = "bookingTime";
        }

        // Build sort
        Sort sort = Sort.by(sortBy);

        if (direction.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        // Create pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch paginated + sorted data from DB
        Page<Booking> bookings = bookingRepository.findByProvider(provider, pageable);

        return bookings.map(this::mapToResponse);

    }

    @Override
    public BookingResponse acceptBooking(Long bookingId) {

        Booking booking = getBookingOrThrow(bookingId);
        String email = getLoggedInUserEmail();

        // Only the provider who owns this booking can accept it
        if (!booking.getProvider().getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not allowed to accept this booking");
        }

        // Only REQUESTED bookings can be accepted
        if (booking.getStatus() != BookingStatus.REQUESTED) {
            throw new BadRequestException("Only requested bookings can be accepted");
        }

        booking.setStatus(BookingStatus.ACCEPTED);

        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse rejectBooking(Long bookingId) {

        Booking booking = getBookingOrThrow(bookingId);
        String email = getLoggedInUserEmail();

        // Authorization check
        if (!booking.getProvider().getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not allowed to reject this booking");
        }

        // Only REQUESTED bookings can be rejected
        if (booking.getStatus() != BookingStatus.REQUESTED) {
            throw new BadRequestException("Only requested bookings can be rejected");
        }

        booking.setStatus(BookingStatus.REJECTED);

        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse completeBooking(Long bookingId) {

        Booking booking = getBookingOrThrow(bookingId);
        String email = getLoggedInUserEmail();

        // Authorization check
        if (!booking.getProvider().getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not allowed to complete this booking");
        }

        // Only ACCEPTED bookings can be completed
        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new BadRequestException("Only accepted bookings can be completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);

        return mapToResponse(bookingRepository.save(booking));
    }

    // Centralized method to fetch booking or throw exception
    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    // Utility to fetch logged-in user's email from security context
    private String getLoggedInUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Maps Booking entity to BookingResponse DTO
    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .status(booking.getStatus().name())
                .bookingTime(booking.getBookingTime())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .providerId(booking.getProvider().getId())
                .providerName(booking.getProvider().getBusinessName())
                .serviceName(booking.getServiceOffering().getServiceCategory().getName())
                .build();
    }
}