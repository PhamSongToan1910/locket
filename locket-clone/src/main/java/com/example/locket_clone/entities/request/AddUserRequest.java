package com.example.locket_clone.entities.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUserRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
}
