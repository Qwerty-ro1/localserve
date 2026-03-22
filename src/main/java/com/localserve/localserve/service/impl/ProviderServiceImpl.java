package com.localserve.localserve.service.impl;

import com.localserve.localserve.dto.ProviderRequest;
import com.localserve.localserve.dto.ProviderResponse;
import com.localserve.localserve.entity.Provider;
import com.localserve.localserve.entity.Role;
import com.localserve.localserve.entity.ServiceOffering;
import com.localserve.localserve.entity.User;
import com.localserve.localserve.repository.ProviderRepository;
import com.localserve.localserve.repository.UserRepository;
import com.localserve.localserve.service.ProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;

    // Haversine Earth radius constant in km
    private static final double EARTH_RADIUS = 6371;

    @Override
    @Transactional
    public ProviderResponse registerProvider(ProviderRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(providerRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Provider already registered");
        }

        Provider provider = Provider.builder()
                .user(user)
                .businessName(request.getBusinessName())
                .description(request.getDescription())
                .experienceYears(request.getExperienceYears())
                .serviceRadius(request.getServiceRadius())
                .location(request.getLocation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .rating(0.0)
                .build();

        // Create offerings
        List<ServiceOffering> offerings = request.getOfferings().stream()
                .map(name -> ServiceOffering.builder()
                        .name(name)
                        .provider(provider)
                        .build())
                .collect(Collectors.toList());

        provider.setOfferings(offerings);

        providerRepository.save(provider);

        // Update user role to PROVIDER
        user.setRole(Role.PROVIDER);
        userRepository.save(user);

        return mapToResponse(provider);
    }

    @Override
    public ProviderResponse getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        return mapToResponse(provider);
    }

    @Override
    @Transactional
    public ProviderResponse updateProvider(Long id, ProviderRequest request) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        provider.setBusinessName(request.getBusinessName());
        provider.setDescription(request.getDescription());
        provider.setExperienceYears(request.getExperienceYears());
        provider.setServiceRadius(request.getServiceRadius());
        provider.setLocation(request.getLocation());
        provider.setLatitude(request.getLatitude());
        provider.setLongitude(request.getLongitude());

        // Update offerings
        provider.getOfferings().clear();
        List<ServiceOffering> offerings = request.getOfferings().stream()
                .map(name -> ServiceOffering.builder()
                        .name(name)
                        .provider(provider)
                        .build())
                .collect(Collectors.toList());
        provider.setOfferings(offerings);

        providerRepository.save(provider);

        return mapToResponse(provider);
    }

    @Override
    public ProviderResponse getMyProvider() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Provider provider = providerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        return mapToResponse(provider);
    }

    @Override
    public List<ProviderResponse> searchProviders(String offeringName, double lat, double lon, double radius) {
        List<Provider> allProviders;

        if (offeringName == null || offeringName.isEmpty()) {
            allProviders = providerRepository.findAll();
        } else {
            allProviders = providerRepository.findByOfferings_NameContainingIgnoreCase(offeringName);
        }

        return allProviders.stream()
                .filter(p -> calculateDistance(lat, lon, p.getLatitude(), p.getLongitude()) <= radius)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Haversine formula to calculate distance between two lat/lon points
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    // Mapper to ProviderResponse DTO
    private ProviderResponse mapToResponse(Provider provider) {
        ProviderResponse response = new ProviderResponse();
        response.setId(provider.getId());
        response.setBusinessName(provider.getBusinessName());
        response.setDescription(provider.getDescription());
        response.setExperienceYears(provider.getExperienceYears());
        response.setServiceRadius(provider.getServiceRadius());
        response.setRating(provider.getRating());
        response.setLocation(provider.getLocation());
        response.setLatitude(provider.getLatitude());
        response.setLongitude(provider.getLongitude());
        response.setOfferings(provider.getOfferings()
                .stream()
                .map(ServiceOffering::getName)
                .collect(Collectors.toList()));
        return response;
    }
}