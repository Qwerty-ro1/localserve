package com.localserve.localserve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.localserve.localserve.dto.ProviderRequest;
import com.localserve.localserve.dto.ProviderResponse;
import com.localserve.localserve.security.CustomUserDetailsService;
import com.localserve.localserve.security.JwtService;
import com.localserve.localserve.service.ProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProviderController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProviderService providerService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // REGISTER

    @Test
    @DisplayName("Should register provider successfully")
    @WithMockUser(roles = {"USER"})
    void registerProvider_success() throws Exception {

        ProviderRequest request = new ProviderRequest();
        request.setBusinessName("Test Business");
        request.setDescription("Plumbing services");
        request.setLocation("Nagpur");
        request.setServiceCategoryIds(List.of(1L));

        ProviderResponse response = ProviderResponse.builder()
                .id(1L)
                .businessName("Test Business")
                .build();

        when(providerService.registerProvider(any())).thenReturn(response);

        mockMvc.perform(post("/api/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Provider registered successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.businessName").value("Test Business"));

        verify(providerService).registerProvider(any());
    }

    @Test
    @DisplayName("Should fail when request is invalid")
    @WithMockUser(roles = {"USER"})
    void registerProvider_invalidRequest() throws Exception {

        ProviderRequest request = new ProviderRequest(); // empty → triggers validation

        mockMvc.perform(post("/api/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // GET BY ID

    @Test
    @DisplayName("Should get provider by id")
    void getProvider_success() throws Exception {

        ProviderResponse response = ProviderResponse.builder()
                .id(1L)
                .businessName("Test Business")
                .build();

        when(providerService.getProviderById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/providers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Provider details retrieved"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // UPDATE

    @Test
    @DisplayName("Should update provider successfully")
    @WithMockUser(roles = {"USER"})
    void updateProvider_success() throws Exception {

        ProviderRequest request = new ProviderRequest();
        request.setBusinessName("Updated Business");

        ProviderResponse response = ProviderResponse.builder()
                .id(1L)
                .businessName("Updated Business")
                .build();

        when(providerService.updateProvider(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/providers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Provider profile updated"))
                .andExpect(jsonPath("$.data.businessName").value("Updated Business"));

        verify(providerService).updateProvider(eq(1L), any());
    }

    // SEARCH

    @Test
    @DisplayName("Should search providers successfully")
    void searchProviders_success() throws Exception {

        ProviderResponse response = ProviderResponse.builder()
                .id(1L)
                .businessName("Nearby Service")
                .build();

        Page<ProviderResponse> page = new PageImpl<>(List.of(response));

        when(providerService.searchProviders(
                any(), anyDouble(), anyDouble(), anyDouble(),
                anyInt(), anyInt(), anyString(), anyString()
        )).thenReturn(page);

        mockMvc.perform(get("/api/providers/search")
                        .param("latitude", "21.1458")
                        .param("longitude", "79.0882")
                        .param("radius", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Search results retrieved"))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }
}