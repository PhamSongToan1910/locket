package com.example.locket_clone.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JWTToken {
    private String accessToken;
    private String refreshToken;
}
