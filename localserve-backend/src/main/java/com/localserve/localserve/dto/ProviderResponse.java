package com.localserve.localserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProviderResponse {

    private Long id;
    private String businessName;
    private String description;
    private int experienceYears;
    private double serviceRadius;
    private double rating;
    private String location;
    private double latitude;
    private double longitude;
    private Double distance;
    private List<String> services;
    private List<ServiceOfferingDto> offerings;  // new — includes id for booking

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceOfferingDto {
        private Long id;
        private String categoryName;
    }
}