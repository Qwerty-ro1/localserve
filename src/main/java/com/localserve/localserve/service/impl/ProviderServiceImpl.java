package com.localserve.localserve.service.impl;

import com.localserve.localserve.dto.ProviderRequest;
import com.localserve.localserve.dto.ProviderResponse;
import com.localserve.localserve.entity.Provider;
import com.localserve.localserve.entity.Role;
import com.localserve.localserve.entity.ServiceOffering;
import com.localserve.localserve.entity.User;
import com.localserve.localserve.exception.BadRequestException;
import com.localserve.localserve.exception.ResourceNotFoundException;
import com.localserve.localserve.repository.ProviderRepository;
import com.localserve.localserve.repository.UserRepository;
import com.localserve.localserve.service.ProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;

    // Constant used in Haversine formula to calculate distance
    private static final double EARTH_RADIUS = 6371;

    // Registers a provider for the currently logged-in user
    @Override
    @Transactional
    public ProviderResponse registerProvider(ProviderRequest request) {

        // Get logged-in user email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch user from database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Prevent duplicate provider registration
        if (providerRepository.findByUser(user).isPresent()) {
            throw new BadRequestException("Provider already registered");
        }

        // Create provider entity
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

        // Create service offerings
        List<ServiceOffering> offerings = request.getOfferings().stream()
                .map(name -> ServiceOffering.builder()
                        .name(name)
                        .provider(provider)
                        .build())
                .collect(Collectors.toList());

        provider.setOfferings(offerings);

        // Save provider
        providerRepository.save(provider);

        // Update user role to PROVIDER
        user.setRole(Role.PROVIDER);
        userRepository.save(user);

        return mapToResponse(provider);
    }

    // Fetch provider by ID
    @Override
    public ProviderResponse getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        return mapToResponse(provider);
    }

    // Update provider details
    @Override
    @Transactional
    public ProviderResponse updateProvider(Long id, ProviderRequest request) {

        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        // Update fields
        provider.setBusinessName(request.getBusinessName());
        provider.setDescription(request.getDescription());
        provider.setExperienceYears(request.getExperienceYears());
        provider.setServiceRadius(request.getServiceRadius());
        provider.setLocation(request.getLocation());
        provider.setLatitude(request.getLatitude());
        provider.setLongitude(request.getLongitude());

        // Replace existing offerings
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

    // Get provider associated with the logged-in user
    @Override
    public ProviderResponse getMyProvider() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Provider provider = providerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        return mapToResponse(provider);
    }

    // Search providers by offering name and distance radius
    @Override
    public List<ProviderResponse> searchProviders(String offeringName, double lat, double lon, double radius) {

        List<Provider> providers;

        // Fetch providers based on offering filter
        if (offeringName == null || offeringName.isEmpty()) {
            providers = providerRepository.findAll();
        } else {
            providers = providerRepository.findByOfferings_NameContainingIgnoreCase(offeringName);
        }

        // Filter providers within radius
        return providers.stream()
                .filter(p -> calculateDistance(lat, lon, p.getLatitude(), p.getLongitude()) <= radius)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Haversine formula to calculate distance between two coordinates
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    // Convert Provider entity to response DTO
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