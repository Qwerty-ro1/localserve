package com.localserve.localserve.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
