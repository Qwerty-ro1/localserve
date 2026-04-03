package com.localserve.localserve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localserve.localserve.dto.BookingResponse;
import com.localserve.localserve.dto.CreateBookingRequest;
import com.localserve.localserve.security.CustomUserDetailsService;
import com.localserve.localserve.security.JwtService;
import com.localserve.localserve.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // CREATE BOOKING

    @Test
    @DisplayName("Should create booking successfully")
    @WithMockUser(username = "user@test.com", roles = {"USER"})
    void createBooking_success() throws Exception {

        CreateBookingRequest request = new CreateBookingRequest();
        request.setServiceOfferingId(1L);
        request.setBookingTime(LocalDateTime.now().plusDays(1));

        BookingResponse response = BookingResponse.builder()
                .id(101L)
                .status("REQUESTED")
                .build();

        when(bookingService.createBooking(anyString(), anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Booking request sent successfully"))
                .andExpect(jsonPath("$.data.id").value(101))
                .andExpect(jsonPath("$.data.status").value("REQUESTED"));

        verify(bookingService).createBooking(anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("Should fail when request is invalid")
    @WithMockUser
    void createBooking_invalidRequest() throws Exception {

        CreateBookingRequest request = new CreateBookingRequest(); // missing fields

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    // USER BOOKINGS

    @Test
    @DisplayName("Should retrieve user bookings")
    @WithMockUser(username = "user@test.com", roles = {"USER"})
    void getUserBookings_success() throws Exception {

        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .build();

        Page<BookingResponse> page = new PageImpl<>(List.of(response));

        when(bookingService.getUserBookings(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User bookings retrieved"))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    // PROVIDER BOOKINGS

    @Test
    @DisplayName("Should retrieve provider bookings")
    @WithMockUser(username = "provider@test.com", roles = {"PROVIDER"})
    void getProviderBookings_success() throws Exception {

        BookingResponse response = BookingResponse.builder()
                .id(2L)
                .build();

        Page<BookingResponse> page = new PageImpl<>(List.of(response));

        when(bookingService.getProviderBookings(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        mockMvc.perform(get("/api/bookings/provider/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Provider incoming bookings retrieved"))
                .andExpect(jsonPath("$.data.content[0].id").value(2));
    }

    // ACCEPT

    @Test
    @DisplayName("Should accept booking")
    @WithMockUser(username = "provider@test.com", roles = {"PROVIDER"})
    void acceptBooking_success() throws Exception {

        BookingResponse response = BookingResponse.builder()
                .id(10L)
                .status("ACCEPTED")
                .build();

        when(bookingService.acceptBooking(10L)).thenReturn(response);

        mockMvc.perform(put("/api/bookings/10/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking accepted"))
                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));
    }

    // REJECT

    @Test
    @DisplayName("Should reject booking")
    @WithMockUser(username = "provider@test.com", roles = {"PROVIDER"})
    void rejectBooking_success() throws Exception {

        BookingResponse response = BookingResponse.builder()
                .id(10L)
                .status("REJECTED")
                .build();

        when(bookingService.rejectBooking(10L)).thenReturn(response);

        mockMvc.perform(put("/api/bookings/10/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking rejected"))
                .andExpect(jsonPath("$.data.status").value("REJECTED"));
    }

    // COMPLETE

    @Test
    @DisplayName("Should complete booking")
    @WithMockUser(username = "provider@test.com", roles = {"PROVIDER"})
    void completeBooking_success() throws Exception {

        BookingResponse response = BookingResponse.builder()
                .id(10L)
                .status("COMPLETED")
                .build();

        when(bookingService.completeBooking(10L)).thenReturn(response);

        mockMvc.perform(put("/api/bookings/10/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking marked as completed"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }
}