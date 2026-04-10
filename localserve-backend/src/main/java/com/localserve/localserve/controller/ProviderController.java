package com.localserve.localserve.controller;

import com.localserve.localserve.dto.ApiResponse;
import com.localserve.localserve.dto.ProviderRequest;
import com.localserve.localserve.dto.ProviderResponse;
import com.localserve.localserve.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ProviderResponse>> registerProvider(@RequestBody @Valid ProviderRequest request) {
        ProviderResponse response = providerService.registerProvider(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Provider registered successfully", response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ProviderResponse>> getMyProvider() {
        ProviderResponse response = providerService.getMyProvider();
        return ResponseEntity.ok(ApiResponse.success("Provider profile retrieved", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProviderResponse>> getProvider(@PathVariable Long id) {
        ProviderResponse response = providerService.getProviderById(id);
        return ResponseEntity.ok(ApiResponse.success("Provider details retrieved", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProviderResponse>> updateProvider(
            @PathVariable Long id,
            @RequestBody ProviderRequest request) {
        ProviderResponse response = providerService.updateProvider(id, request);
        return ResponseEntity.ok(ApiResponse.success("Provider profile updated", response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProviderResponse>>> searchProviders(
            @RequestParam(required = false) Long serviceCategoryId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "distance") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Page<ProviderResponse> providers = providerService.searchProviders(
                serviceCategoryId, latitude, longitude, radius, page, size, sortBy, direction
        );
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved", providers));
    }
}