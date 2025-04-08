package com.example.locket_clone.entities.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Objects;

@Data
public class LoginVM {
    @JsonProperty("email")
    private String email;
    private String avt;

    public boolean validateRequest() {
        return Objects.nonNull(this.email);
    }
}
