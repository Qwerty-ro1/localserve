package com.localserve.localserve.controller;

import com.localserve.localserve.dto.ProviderRequest;
import com.localserve.localserve.dto.ProviderResponse;
import com.localserve.localserve.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ProviderResponse registerProvider(@RequestBody ProviderRequest request) {
        return providerService.registerProvider(request);
    }



    @GetMapping("/{id}")
    public ProviderResponse getProvider(@PathVariable Long id) {
        return providerService.getProviderById(id);
    }

    @PutMapping("/{id}")
    public ProviderResponse updateProvider(@PathVariable Long id,
                                           @RequestBody ProviderRequest request) {
        return providerService.updateProvider(id, request);
    }

    @GetMapping("/search")
    public List<ProviderResponse> searchProviders(
            @RequestParam String offering,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius // in km
    ) {
        return providerService.searchProviders(offering, latitude, longitude, radius);
    }
}