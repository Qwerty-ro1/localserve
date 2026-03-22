package com.localserve.localserve.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

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
    private List<String> offerings;
}