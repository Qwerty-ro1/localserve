package com.localserve.localserve.controller;

import com.localserve.localserve.dto.ApiResponse;
import com.localserve.localserve.dto.LoginRequest;
import com.localserve.localserve.dto.LoginResponse;
import com.localserve.localserve.dto.RegisterRequest;
import com.localserve.localserve.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody @Valid RegisterRequest request) {
        String message = authService.register(request);
        // Returns success: true with the registration message
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message, null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        // Return LoginResponse with token and user details
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<String>> user() {
        return ResponseEntity.ok(ApiResponse.success("Status check", "Hello User"));
    }
}