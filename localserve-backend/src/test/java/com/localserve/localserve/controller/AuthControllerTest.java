package com.localserve.localserve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localserve.localserve.dto.LoginRequest;
import com.localserve.localserve.dto.LoginResponse;
import com.localserve.localserve.dto.RegisterRequest;
import com.localserve.localserve.security.CustomUserDetailsService;
import com.localserve.localserve.security.JwtService;
import com.localserve.localserve.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── REGISTER ────────────────────────────────────────────────

    @Test
    @DisplayName("POST /register - Success: Returns 201 Created")
    void register_ShouldReturnCreated_WhenInputIsValid() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Omkar")
                .email("test@test.com")
                .password("password123")
                .build();

        when(authService.register(any())).thenReturn("User registered successfully");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        verify(authService, times(1)).register(any());
    }

    @Test
    @DisplayName("POST /register - Failure: Returns 400 when name is blank")
    void register_ShouldFail_WhenNameIsBlank() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("")
                .email("test@test.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("POST /register - Failure: Returns 400 when email is invalid")
    void register_ShouldFail_WhenEmailIsInvalid() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Omkar")
                .email("not-an-email")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(authService);
    }

    // ── LOGIN ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /login - Success: Returns JWT token and user details")
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("test@test.com")
                .password("password123")
                .build();

        // authService.login() returns LoginResponse, not a plain String
        LoginResponse loginResponse = LoginResponse.builder()
                .token("mocked-jwt-token")
                .email("test@test.com")
                .name("Omkar")
                .role("USER")
                .build();

        when(authService.login(any())).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.role").value("USER"));

        verify(authService).login(any());
    }

    @Test
    @DisplayName("POST /login - Failure: Returns 400 when email is missing")
    void login_ShouldFail_WhenEmailIsMissing() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("POST /login - Failure: Returns 400 when password is missing")
    void login_ShouldFail_WhenPasswordIsMissing() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("test@test.com")
                .password("")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(authService);
    }

    // ── STATUS CHECK ─────────────────────────────────────────────

    @Test
    @DisplayName("GET /user - Success: Returns hello message")
    void user_ShouldReturnStatus() throws Exception {
        mockMvc.perform(get("/api/auth/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Hello User"));
    }
}