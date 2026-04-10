package com.localserve.localserve.dto;

import com.localserve.localserve.entity.AddressLabel;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserAddressRequest {

    @NotNull(message = "Label is required")
    private AddressLabel label;

    @NotBlank(message = "Address is required")
    private String addressLine;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    private boolean isDefault;
}