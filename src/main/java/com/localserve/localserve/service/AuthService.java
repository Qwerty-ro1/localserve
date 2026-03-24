package com.localserve.localserve.service;

import com.localserve.localserve.dto.LoginRequest;
import com.localserve.localserve.dto.RegisterRequest;

public interface AuthService {

    // Registers a new user
    String register(RegisterRequest request);

    // Authenticates user and returns JWT token
    String login(LoginRequest request);
}