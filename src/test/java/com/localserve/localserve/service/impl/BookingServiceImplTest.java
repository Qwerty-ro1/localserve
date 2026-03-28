package com.localserve.localserve.service.impl;

import com.localserve.localserve.dto.BookingResponse;
import com.localserve.localserve.entity.*;
import com.localserve.localserve.exception.BadRequestException;
import com.localserve.localserve.exception.ResourceNotFoundException;
import com.localserve.localserve.exception.UnauthorizedException;
import com.localserve.localserve.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProviderRepository providerRepository;
    @Mock private ServiceOfferingRepository serviceOfferingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User providerUser;
    private Provider provider;
    private ServiceOffering offering;

    @BeforeEach
    void setup() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("provider@test.com", null)
        );

        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setName("User");

        providerUser = new User();
        providerUser.setId(2L);
        providerUser.setEmail("provider@test.com");

        provider = new Provider();
        provider.setId(10L);
        provider.setUser(providerUser);
        provider.setBusinessName("Test Business");

        MasterServiceCategory category = new MasterServiceCategory();
        category.setId(1L);
        category.setName("Plumbing");

        offering = new ServiceOffering();
        offering.setId(100L);
        offering.setProvider(provider);
        offering.setServiceCategory(category);
    }

    // =========================
    // CREATE BOOKING
    // =========================

    @Test
    void shouldCreateBookingSuccessfully() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(serviceOfferingRepository.findById(100L))
                .thenReturn(Optional.of(offering));

        when(bookingRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        BookingResponse response = bookingService.createBooking(
                "user@test.com",
                100L,
                LocalDateTime.now()
        );

        assertNotNull(response);
        assertEquals("REQUESTED", response.getStatus());
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.createBooking("x", 1L, LocalDateTime.now()));
    }

    @Test
    void shouldThrowWhenServiceNotFound() {

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(serviceOfferingRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.createBooking("x", 1L, LocalDateTime.now()));
    }

    @Test
    void shouldThrowWhenUserBooksOwnService() {

        providerUser.setId(1L); // same as user

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(serviceOfferingRepository.findById(any()))
                .thenReturn(Optional.of(offering));

        assertThrows(BadRequestException.class,
                () -> bookingService.createBooking("x", 1L, LocalDateTime.now()));
    }

    // =========================
    // USER BOOKINGS
    // =========================

    @Test
    void shouldReturnUserBookings() {

        Page<Booking> page = new PageImpl<>(List.of(buildBooking()));

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findByUser(eq(user), any()))
                .thenReturn(page);

        Page<BookingResponse> result =
                bookingService.getUserBookings("user@test.com", 0, 10, "bookingTime", "asc");

        assertFalse(result.isEmpty());
    }

    // =========================
    // PROVIDER BOOKINGS
    // =========================

    @Test
    void shouldReturnProviderBookings() {

        Page<Booking> page = new PageImpl<>(List.of(buildBooking()));

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(providerUser));

        when(providerRepository.findByUser(providerUser))
                .thenReturn(Optional.of(provider));

        when(bookingRepository.findByProvider(eq(provider), any()))
                .thenReturn(page);

        Page<BookingResponse> result =
                bookingService.getProviderBookings("provider@test.com", 0, 10, "bookingTime", "asc");

        assertFalse(result.isEmpty());
    }

    // =========================
    // ACCEPT BOOKING
    // =========================

    @Test
    void shouldAcceptBooking() {

        Booking booking = buildBooking();
        booking.setStatus(BookingStatus.REQUESTED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponse response = bookingService.acceptBooking(1L);

        assertEquals("ACCEPTED", response.getStatus());
    }

    @Test
    void shouldThrowUnauthorizedWhenAccepting() {

        providerUser.setEmail("wrong@test.com");

        Booking booking = buildBooking();
        booking.setStatus(BookingStatus.REQUESTED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThrows(UnauthorizedException.class,
                () -> bookingService.acceptBooking(1L));
    }

    @Test
    void shouldThrowWhenAcceptingNonRequested() {

        Booking booking = buildBooking();
        booking.setStatus(BookingStatus.ACCEPTED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class,
                () -> bookingService.acceptBooking(1L));
    }

    // =========================
    // HELPER
    // =========================

    private Booking buildBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setProvider(provider);
        booking.setServiceOffering(offering);
        booking.setStatus(BookingStatus.REQUESTED);
        booking.setBookingTime(LocalDateTime.now());
        return booking;
    }
}