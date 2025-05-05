package com.example.locket_clone.entities.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginAdminRequest {
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;

    public boolean validateRequest() {
        return this.email != null && this.password != null;
    }
}
