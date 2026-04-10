package com.localserve.localserve.dto;

import com.localserve.localserve.entity.AddressLabel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAddressResponse {
    private Long id;
    private AddressLabel label;;
    private String addressLine;
    private double latitude;
    private double longitude;
    private boolean isDefault;
}