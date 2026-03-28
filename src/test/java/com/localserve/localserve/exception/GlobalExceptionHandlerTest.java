package com.localserve.localserve.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localserve.localserve.controller.AuthController;
import com.localserve.localserve.dto.LoginRequest;
import com.localserve.localserve.dto.RegisterRequest;
import com.localserve.localserve.security.CustomUserDetailsService;
import com.localserve.localserve.security.JwtService;
import com.localserve.localserve.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class) // IMPORTANT: include your handler
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Validation Error Test
    @Test
    @DisplayName("POST /register - Validation Failure")
    void register_validationFailure() throws Exception {

        RegisterRequest invalidRequest = new RegisterRequest(); // empty fields

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.data").exists());
    }

    // BadRequestException Test
    @Test
    @DisplayName("POST /register - Bad Request Exception")
    void register_badRequestException() throws Exception {

        RegisterRequest request = new RegisterRequest("Omkar", "test@test.com", "password123");

        when(authService.register(any()))
                .thenThrow(new BadRequestException("User already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User already exists"));
    }

    // Unauthorized Exception Test (401)
    @Test
    @DisplayName("POST /login - Unauthorized")
    void login_unauthorized() throws Exception {

        LoginRequest request = new LoginRequest("test@test.com", "wrong-password");

        when(authService.login(any()))
                .thenThrow(new UnauthorizedException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    // Generic Exception (500)
    @Test
    @DisplayName("POST /login - Internal Server Error")
    void login_genericException() throws Exception {

        LoginRequest request = new LoginRequest("test@test.com", "password123");

        when(authService.login(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));
    }

    // Invalid JSON Test
    @Test
    @DisplayName("POST /register - Invalid JSON")
    void register_invalidJson() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body"));
    }
}