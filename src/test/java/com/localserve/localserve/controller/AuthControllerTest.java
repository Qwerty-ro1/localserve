package com.localserve.localserve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localserve.localserve.dto.LoginRequest;
import com.localserve.localserve.dto.RegisterRequest;
import com.localserve.localserve.entity.User;
import com.localserve.localserve.security.CustomUserDetailsService;
import com.localserve.localserve.security.JwtService;
import com.localserve.localserve.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disables Security filters to test Controller logic in isolation
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService; // Fills the gap for your JwtAuthFilter

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // REGISTER ENDPOINT

    @Test
    @DisplayName("POST /register - Success: Returns 201 Created")
    void register_ShouldReturnCreated_WhenInputIsValid() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest("Omkar", "test@test.com", "password123");
        when(authService.register(any())).thenReturn("User registered successfully");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        // Verify: The service was called exactly once
        verify(authService, times(1)).register(any());
    }

    @Test
    @DisplayName("POST /register - Failure: Returns 400 Bad Request on empty input")
    void register_ShouldFail_WhenInputIsInvalid() throws Exception {
        // Arrange: Empty object triggers @Valid validation failures
        RegisterRequest request = new RegisterRequest();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verify: The service was NEVER called due to validation intercept
        verifyNoInteractions(authService);
    }

    // LOGIN ENDPOINT

    @Test
    @DisplayName("POST /login - Success: Returns JWT Token")
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("test@test.com", "password123");
        when(authService.login(any())).thenReturn("mocked-jwt-token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("mocked-jwt-token"));

        verify(authService).login(any());
    }

    @Test
    @DisplayName("POST /login - Failure: Returns 400 Bad Request on invalid input")
    void login_ShouldFail_WhenInputIsInvalid() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verify: Service layer is protected from bad data
        verifyNoInteractions(authService);
    }

    // USER ENDPOINT (STATUS CHECK)

    @Test
    @DisplayName("GET /user - Success: Returns hello message")
    void user_ShouldReturnStatus() throws Exception {
        mockMvc.perform(get("/api/auth/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Hello User"));
    }
}