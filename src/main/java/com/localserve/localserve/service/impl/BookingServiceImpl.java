package com.localserve.localserve.service.impl;

import com.localserve.localserve.dto.BookingResponse;
import com.localserve.localserve.entity.*;
import com.localserve.localserve.exception.BadRequestException;
import com.localserve.localserve.exception.ResourceNotFoundException;
import com.localserve.localserve.exception.UnauthorizedException;
import com.localserve.localserve.repository.*;
import com.localserve.localserve.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ServiceOffering serviceOffering = serviceOfferingRepository.findById(serviceOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Service offering not found"));

        Provider provider = serviceOffering.getProvider();

        if (provider.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot book your own service");
        }

        Booking booking = Booking.builder()
                .user(user)
                .provider(provider)
                .serviceOffering(serviceOffering)
                .status(BookingStatus.REQUESTED)
                .bookingTime(bookingTime)
                .build();

        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponse> getUserBookings(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getProviderBookings(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Provider provider = providerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        return bookingRepository.findByProvider(provider)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BookingResponse acceptBooking(Long bookingId) {

        Booking booking = getBookingOrThrow(bookingId);
        String email = getLoggedInUserEmail();

        if (!booking.getProvider().getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not allowed to accept this booking");
        }

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

        if (!booking.getProvider().getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not allowed to reject this booking");
        }

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

        if (!booking.getProvider().getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not allowed to complete this booking");
        }

        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new BadRequestException("Only accepted bookings can be completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);

        return mapToResponse(bookingRepository.save(booking));
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private String getLoggedInUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .status(booking.getStatus().name())
                .bookingTime(booking.getBookingTime())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .providerId(booking.getProvider().getId())
                .providerName(booking.getProvider().getBusinessName())
                .serviceName(booking.getServiceOffering().getName())
                .build();
    }
}