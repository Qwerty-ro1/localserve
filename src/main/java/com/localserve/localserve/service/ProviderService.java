package com.localserve.localserve.service;

import com.localserve.localserve.dto.ProviderRequest;
import com.localserve.localserve.dto.ProviderResponse;

import java.util.List;

public interface ProviderService {

    ProviderResponse registerProvider(ProviderRequest request);

    ProviderResponse getProviderById(Long id);

    ProviderResponse updateProvider(Long id, ProviderRequest request);

    List<ProviderResponse> searchProviders(String offering, double lat, double lon, double radius);

    ProviderResponse getMyProvider();
}