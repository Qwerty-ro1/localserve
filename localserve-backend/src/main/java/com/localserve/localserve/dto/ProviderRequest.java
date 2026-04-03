package com.localserve.localserve.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ProviderRequest {

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "Description is required")
    private String description;

    private int experienceYears = 0; // optional, default 0

    private double serviceRadius = 15.0; // optional, default radius

    @NotBlank(message = "Location is required")
    private String location;

    private Double latitude;  // optional, can be filled via geocoding
    private Double longitude; // optional

    @NotEmpty(message = "At least one service category must be selected")
    private List<Long> serviceCategoryIds;
}