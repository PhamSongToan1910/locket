package com.example.locket_clone.entities.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String access_token;
    private String refresh_token;
    private boolean is_complete;
}
