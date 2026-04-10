package com.localserve.localserve.service.impl;

import com.localserve.localserve.dto.LoginRequest;
import com.localserve.localserve.dto.RegisterRequest;
import com.localserve.localserve.entity.Role;
import com.localserve.localserve.entity.User;
import com.localserve.localserve.exception.BadRequestException;
import com.localserve.localserve.exception.ResourceNotFoundException;
import com.localserve.localserve.repository.UserRepository;
import com.localserve.localserve.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Initializes Mocks
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService; // Injects the mocks above into this service

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    // --- REGISTER TESTS ---

    @Test
    void register_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password123");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        String response = authService.register(request);

        // Assert
        assertEquals("User registered successfully", response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_Fails_UserAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password123");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    // --- LOGIN TESTS ---

    @Test
    void login_Success() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(anyString())).thenReturn("fake-jwt-token");

        // Act
        String token = String.valueOf(authService.login(request));

        // Assert
        assertNotNull(token);
        assertEquals("fake-jwt-token", token);
    }

    @Test
    void login_Fails_UserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest("wrong@example.com", "password123");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void login_Fails_WrongPassword() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> authService.login(request));
    }
}