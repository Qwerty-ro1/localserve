package com.localserve.localserve.service;

import com.localserve.localserve.dto.UserAddressRequest;
import com.localserve.localserve.dto.UserAddressResponse;
import java.util.List;

public interface UserAddressService {
    List<UserAddressResponse> getAddresses(String email);
    UserAddressResponse addAddress(String email, UserAddressRequest request);
    UserAddressResponse updateAddress(String email, Long addressId, UserAddressRequest request);
    void deleteAddress(String email, Long addressId);
    UserAddressResponse setDefault(String email, Long addressId);
}