package com.localserve.localserve.controller;

import com.localserve.localserve.dto.ApiResponse;
import com.localserve.localserve.dto.UserAddressRequest;
import com.localserve.localserve.dto.UserAddressResponse;
import com.localserve.localserve.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final UserAddressService userAddressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserAddressResponse>>> getAll() {
        String email = getEmail();
        return ResponseEntity.ok(
                ApiResponse.success("Addresses fetched",
                        userAddressService.getAddresses(email))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserAddressResponse>> add(
            @Valid @RequestBody UserAddressRequest request) {
        String email = getEmail();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Address added",
                        userAddressService.addAddress(email, request))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserAddressResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserAddressRequest request) {
        String email = getEmail();
        return ResponseEntity.ok(
                ApiResponse.success("Address updated",
                        userAddressService.updateAddress(email, id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        String email = getEmail();
        userAddressService.deleteAddress(email, id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted", null));
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<UserAddressResponse>> setDefault(
            @PathVariable Long id) {
        String email = getEmail();
        return ResponseEntity.ok(
                ApiResponse.success("Default address updated",
                        userAddressService.setDefault(email, id))
        );
    }

    private String getEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }
}