package com.localserve.localserve.service.impl;

import com.localserve.localserve.dto.LoginRequest;
import com.localserve.localserve.dto.RegisterRequest;
import com.localserve.localserve.entity.Role;
import com.localserve.localserve.entity.User;
import com.localserve.localserve.exception.BadRequestException;
import com.localserve.localserve.exception.ResourceNotFoundException;
import com.localserve.localserve.repository.UserRepository;
import com.localserve.localserve.security.JwtService;
import com.localserve.localserve.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Registers a new user (no provider creation here)
    @Override
    public String register(RegisterRequest request) {

        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("User already exists with this email");
        }

        // Create user entity
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // all will register as users
                .createdAt(LocalDateTime.now())
                .build();

        // Save user only (provider will be created separately)
        userRepository.save(user);

        return "User registered successfully";
    }

    // Authenticates user and returns JWT token
    @Override
    public String login(LoginRequest request) {

        // Fetch user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        // Generate JWT token
        return jwtService.generateToken(user.getEmail());
    }
}