package com.localserve.localserve.service.impl;

import com.localserve.localserve.dto.ProviderRequest;
import com.localserve.localserve.dto.ProviderResponse;
import com.localserve.localserve.entity.*;
import com.localserve.localserve.exception.BadRequestException;
import com.localserve.localserve.exception.ForbiddenException;
import com.localserve.localserve.exception.ResourceNotFoundException;
import com.localserve.localserve.repository.BookingRepository;
import com.localserve.localserve.repository.MasterServiceCategoryRepository;
import com.localserve.localserve.repository.ProviderRepository;
import com.localserve.localserve.repository.UserRepository;
import com.localserve.localserve.service.ProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final MasterServiceCategoryRepository masterServiceCategoryRepository;
    private final BookingRepository bookingRepository;

    private static final double EARTH_RADIUS = 6371;

    @Override
    @Transactional
    public ProviderResponse registerProvider(ProviderRequest request) {

        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new BadRequestException("Latitude and Longitude are required");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (providerRepository.findByUser(user).isPresent()) {
            throw new BadRequestException("Provider already registered");
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

        request.getServiceCategoryIds().forEach(id -> {
            MasterServiceCategory category = masterServiceCategoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Service category not found"));
            provider.getOfferings().add(
                    ServiceOffering.builder()
                            .serviceCategory(category)
                            .provider(provider)
                            .build()
            );
        });

        providerRepository.save(provider);

        user.setRole(Role.PROVIDER);
        userRepository.save(user);

        return mapToResponse(provider);
    }

    @Override
    public ProviderResponse getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        return mapToResponse(provider);
    }

    @Override
    @Transactional
    public ProviderResponse updateProvider(Long id, ProviderRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        if (!provider.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to update this provider profile");
        }

        provider.setBusinessName(request.getBusinessName());
        provider.setDescription(request.getDescription());
        provider.setExperienceYears(request.getExperienceYears());
        provider.setServiceRadius(request.getServiceRadius());
        provider.setLocation(request.getLocation());
        provider.setLatitude(request.getLatitude());
        provider.setLongitude(request.getLongitude());

        // nullify serviceOffering FK on any bookings referencing these offerings
        // before clearing — prevents FK violation on service_offerings delete
        provider.getOfferings().forEach(offering ->
                bookingRepository.nullifyServiceOffering(offering.getId())
        );

        provider.getOfferings().clear();

        request.getServiceCategoryIds().forEach(catId -> {
            MasterServiceCategory category = masterServiceCategoryRepository.findById(catId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service category not found"));
            provider.getOfferings().add(
                    ServiceOffering.builder()
                            .serviceCategory(category)
                            .provider(provider)
                            .build()
            );
        });

        providerRepository.save(provider);

        return mapToResponse(provider);
    }

    @Override
    public ProviderResponse getMyProvider() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Provider provider = providerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        return mapToResponse(provider);
    }

    @Override
    public Page<ProviderResponse> searchProviders(
            Long serviceCategoryId,
            double lat,
            double lon,
            double radius,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        List<Provider> providers;

        if (serviceCategoryId == null) {
            providers = providerRepository.findAll();
        } else {
            providers = providerRepository.findByOfferings_ServiceCategory_Id(serviceCategoryId);
        }

        List<ProviderResponse> filteredList = providers.stream()
                .map(provider -> {
                    double distance = calculateDistance(lat, lon,
                            provider.getLatitude(), provider.getLongitude());
                    if (distance <= radius) {
                        ProviderResponse response = mapToResponse(provider);
                        response.setDistance(distance);
                        return response;
                    }
                    return null;
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());

        if (!List.of("rating", "distance").contains(sortBy)) sortBy = "distance";

        if (sortBy.equals("rating")) {
            filteredList.sort(direction.equalsIgnoreCase("desc")
                    ? (a, b) -> Double.compare(b.getRating(), a.getRating())
                    : (a, b) -> Double.compare(a.getRating(), b.getRating()));
        } else {
            filteredList.sort(direction.equalsIgnoreCase("desc")
                    ? (a, b) -> Double.compare(b.getDistance(), a.getDistance())
                    : (a, b) -> Double.compare(a.getDistance(), b.getDistance()));
        }

        int start = page * size;
        int end = Math.min(start + size, filteredList.size());

        if (start >= filteredList.size()) {
            return new PageImpl<>(Collections.emptyList(),
                    PageRequest.of(page, size), filteredList.size());
        }

        return new PageImpl<>(filteredList.subList(start, end),
                PageRequest.of(page, size), filteredList.size());
    }

    private double calculateDistance(double lat1, double lon1,
                                     double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return EARTH_RADIUS * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

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

        response.setServices(
                provider.getOfferings().stream()
                        .map(o -> o.getServiceCategory().getName())
                        .collect(Collectors.toList())
        );

        response.setOfferings(
                provider.getOfferings().stream()
                        .map(o -> ProviderResponse.ServiceOfferingDto.builder()
                                .id(o.getId())
                                .categoryName(o.getServiceCategory().getName())
                                .build())
                        .collect(Collectors.toList())
        );

        return response;
    }
}