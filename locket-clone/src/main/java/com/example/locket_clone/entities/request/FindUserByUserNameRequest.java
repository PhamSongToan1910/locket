package com.example.locket_clone.entities.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindUserByUserNameRequest {
    @JsonProperty("username")
    private String userName;

    public boolean validateRequest() {
        return Objects.nonNull(this.userName);
    }
}
