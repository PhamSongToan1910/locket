package com.example.locket_clone.entities.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("device_token")
    private String deviceToken;
}
