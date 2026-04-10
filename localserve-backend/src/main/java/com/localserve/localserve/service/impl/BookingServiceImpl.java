package com.localserve.localserve.service.impl;

import com.localserve.localserve.dto.BookingResponse;
import com.localserve.localserve.dto.UserAddressResponse;
import com.localserve.localserve.entity.*;
import com.localserve.localserve.exception.BadRequestException;
import com.localserve.localserve.exception.ForbiddenException;
import com.localserve.localserve.exception.ResourceNotFoundException;
import com.localserve.localserve.repository.*;
import com.localserve.localserve.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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
    private final UserAddressRepository userAddressRepository;

    @Override
    public BookingResponse createBooking(String email, Long serviceOfferingId,
                                         LocalDateTime bookingTime, Long serviceAddressId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ServiceOffering serviceOffering = serviceOfferingRepository.findById(serviceOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Service offering not found"));

        Provider provider = serviceOffering.getProvider();

        if (provider.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot book your own service");
        }

        // Resolve service address — use provided id, else fall back to user's default
        UserAddress serviceAddress = null;
        if (serviceAddressId != null) {
            serviceAddress = userAddressRepository.findById(serviceAddressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
            // ensure address belongs to this user
            if (!serviceAddress.getUser().getId().equals(user.getId())) {
                throw new ForbiddenException("Address does not belong to you");
            }
        } else {
            // fall back to default address if no address provided
            serviceAddress = userAddressRepository
                    .findByUserIdAndIsDefaultTrue(user.getId())
                    .orElse(null);
        }

        Booking booking = Booking.builder()
                .user(user)
                .provider(provider)
                .serviceOffering(serviceOffering)
                .status(BookingStatus.REQUESTED)
                .bookingTime(bookingTime)
                .serviceAddress(serviceAddress)
                .build();

        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public Page<BookingResponse> getUserBookings(String email, int page, int size,
                                                 String sortBy, String direction) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!List.of("bookingTime", "status").contains(sortBy)) sortBy = "bookingTime";

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Page<Booking> bookings = bookingRepository.findByUser(user, PageRequest.of(page, size, sort));
        return bookings.map(this::mapToResponse);
    }

    @Override
    public Page<BookingResponse> getProviderBookings(String email, int page, int size,
                                                     String sortBy, String direction) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Provider provider = providerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        if (!List.of("bookingTime", "status").contains(sortBy)) sortBy = "bookingTime";

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Page<Booking> bookings = bookingRepository.findByProvider(provider,
                PageRequest.of(page, size, sort));
        return bookings.map(this::mapToResponse);
    }

    @Override
    public BookingResponse acceptBooking(Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        String email = getLoggedInUserEmail();

        if (!booking.getProvider().getUser().getEmail().equals(email)) {
            throw new ForbiddenException("You are not allowed to perform this action");
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
            throw new ForbiddenException("You are not allowed to reject this booking");
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
            throw new ForbiddenException("You are not allowed to complete this booking");
        }
        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new BadRequestException("Only accepted bookings can be completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        return mapToResponse(bookingRepository.save(booking));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

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
                .userPhone(booking.getUser().getPhone())
                .providerId(booking.getProvider().getId())
                .providerName(booking.getProvider().getBusinessName())
                .serviceName(booking.getServiceOffering().getServiceCategory().getName())
                .serviceAddress(mapAddressToResponse(booking.getServiceAddress()))
                .build();
    }

    private UserAddressResponse mapAddressToResponse(UserAddress address) {
        if (address == null) return null;
        return UserAddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .addressLine(address.getAddressLine())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .isDefault(address.isDefault())
                .build();
    }
}