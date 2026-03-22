package com.localserve.localserve.dto;

import lombok.Data;
import java.util.List;


@Data
public class ProviderRequest {

    private String businessName;
    private String description;
    private int experienceYears;
    private double serviceRadius;
    private String location;        // display string
    private double latitude;        // geo-coordinate
    private double longitude;       // geo-coordinate
    private List<String> offerings;
}