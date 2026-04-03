package com.localserve.localserve.service;

import com.localserve.localserve.dto.ProviderRequest;
import com.localserve.localserve.dto.ProviderResponse;
import org.springframework.data.domain.Page;

public interface ProviderService {

    ProviderResponse registerProvider(ProviderRequest request);

    ProviderResponse getProviderById(Long id);

    ProviderResponse updateProvider(Long id, ProviderRequest request);

    Page<ProviderResponse> searchProviders(Long serviceCategoryId, double lat, double lon, double radius, int page, int size, String sortBy, String direction);

    ProviderResponse getMyProvider();
}