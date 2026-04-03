package com.localserve.localserve.service.impl;

import com.localserve.localserve.dto.ProviderRequest;
import com.localserve.localserve.dto.ProviderResponse;
import com.localserve.localserve.entity.*;
import com.localserve.localserve.exception.BadRequestException;
import com.localserve.localserve.exception.ResourceNotFoundException;
import com.localserve.localserve.repository.MasterServiceCategoryRepository;
import com.localserve.localserve.repository.ProviderRepository;
import com.localserve.localserve.repository.UserRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderServiceImplTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MasterServiceCategoryRepository categoryRepository;

    @InjectMocks
    private ProviderServiceImpl providerService;

    @BeforeEach
    void setup() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@example.com", null)
        );
    }

    // =========================
    // REGISTER PROVIDER TESTS
    // =========================

    @Test
    void shouldRegisterProviderSuccessfully() {

        ProviderRequest request = new ProviderRequest();
        request.setBusinessName("Test Business");
        request.setServiceCategoryIds(List.of(1L));

        User user = new User();
        user.setEmail("test@example.com");
        request.setLatitude(12.9716);
        request.setLongitude(77.5946);

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(providerRepository.findByUser(user))
                .thenReturn(Optional.empty());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(new MasterServiceCategory()));


        ProviderResponse response = providerService.registerProvider(request);

        assertNotNull(response);

        verify(providerRepository).save(any());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        ProviderRequest request = new ProviderRequest();
        request.setLatitude(12.9716);
        request.setLongitude(77.5946);

        assertThrows(ResourceNotFoundException.class,
                () -> providerService.registerProvider(request));
    }

    @Test
    void shouldThrowWhenProviderAlreadyExists() {

        User user = new User();
        ProviderRequest request = new ProviderRequest();
        request.setLatitude(12.9716);
        request.setLongitude(77.5946);

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(providerRepository.findByUser(user))
                .thenReturn(Optional.of(new Provider()));

        assertThrows(BadRequestException.class,
                () -> providerService.registerProvider(request));
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {

        ProviderRequest request = new ProviderRequest();
        request.setServiceCategoryIds(List.of(1L));
        request.setLatitude(12.9716);
        request.setLongitude(77.5946);

        User user = new User();

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(providerRepository.findByUser(user))
                .thenReturn(Optional.empty());

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> providerService.registerProvider(request));
    }

    // =========================
    // GET PROVIDER BY ID
    // =========================

    @Test
    void shouldReturnProviderById() {

        Provider provider = new Provider();
        provider.setOfferings(List.of());

        when(providerRepository.findById(1L))
                .thenReturn(Optional.of(provider));

        ProviderResponse response = providerService.getProviderById(1L);

        assertNotNull(response);
    }

    @Test
    void shouldThrowWhenProviderNotFound() {

        when(providerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> providerService.getProviderById(1L));
    }

    // =========================
    // GET MY PROVIDER
    // =========================

    @Test
    void shouldReturnMyProvider() {

        User user = new User();

        Provider provider = new Provider();
        provider.setOfferings(List.of());

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(providerRepository.findByUser(user))
                .thenReturn(Optional.of(provider));

        ProviderResponse response = providerService.getMyProvider();

        assertNotNull(response);
    }

    @Test
    void shouldThrowWhenMyProviderNotFound() {

        User user = new User();

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(providerRepository.findByUser(user))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> providerService.getMyProvider());
    }

    // =========================
    // SEARCH PROVIDERS
    // =========================

    @Test
    void shouldReturnFilteredProviders() {

        Provider provider = new Provider();
        provider.setLatitude(10);
        provider.setLongitude(10);
        provider.setOfferings(List.of());

        when(providerRepository.findAll())
                .thenReturn(List.of(provider));

        Page<ProviderResponse> result = providerService.searchProviders(
                null,
                10,
                10,
                50,
                0,
                10,
                "distance",
                "asc"
        );

        assertFalse(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyPageWhenOutOfRange() {

        when(providerRepository.findAll())
                .thenReturn(List.of());

        Page<ProviderResponse> result = providerService.searchProviders(
                null,
                10,
                10,
                50,
                5,
                10,
                "distance",
                "asc"
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void registerProvider_shouldFail_whenLatLonMissing() {
        User user = new User();
        ProviderRequest request = new ProviderRequest();
        request.setBusinessName("Test");
        request.setServiceCategoryIds(List.of(1L));

        assertThrows(BadRequestException.class,
                () -> providerService.registerProvider(request));
    }
}