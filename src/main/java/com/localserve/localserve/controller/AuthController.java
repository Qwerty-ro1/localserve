package com.localserve.localserve.controller;

import com.localserve.localserve.dto.LoginRequest;
import com.localserve.localserve.dto.RegisterRequest;
import com.localserve.localserve.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/user")
    public String user() {
        return "Hello User";
    }
}
