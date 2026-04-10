package com.localserve.localserve.dto;

import lombok.Data;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

@Data
public class CreateBookingRequest {

    @NotNull(message = "Service offering ID is required")
    private Long serviceOfferingId;

    @NotNull(message = "Booking time is required")
    @Future(message = "Booking time must be in the future")
    private LocalDateTime bookingTime;

    @NotNull(message = "Service address is required")
    private Long serviceAddressId;
}