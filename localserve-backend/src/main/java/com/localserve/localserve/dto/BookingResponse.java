package com.localserve.localserve.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {

    private Long id;
    private String status;
    private LocalDateTime bookingTime;

    private UserAddressResponse serviceAddress;
    private String userPhone;


    private Long userId;
    private String userName;

    private Long providerId;
    private String providerName;

    private String serviceName;
}