package com.localserve.localserve.service;

import com.localserve.localserve.dto.LoginRequest;
import com.localserve.localserve.dto.RegisterRequest;
import com.localserve.localserve.entity.Provider;
import com.localserve.localserve.entity.Role;
import com.localserve.localserve.entity.User;
import com.localserve.localserve.repository.UserRepository;
import com.localserve.localserve.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // REGISTER USER
    public String register(RegisterRequest request) {

        // check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with this email");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .createdAt(LocalDateTime.now())
                .build();

        if (user.getRole() == Role.PROVIDER) {

            Provider provider = Provider.builder()
                    .user(user)
                    .description("New Provider")
                    .experienceYears(0)
                    .serviceRadius(5.0)
                    .rating(0.0)
                    .build();

            // IMPORTANT: set mapping
            user.setProvider(provider);
        }


        userRepository.save(user);

        return "User registered successfully";
    }

    // LOGIN USER (Returns JWT)
    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // generate JWT token
        return jwtService.generateToken(user.getEmail());
    }
}