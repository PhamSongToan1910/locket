package com.example.locket_clone.entities.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeviceTokenRequest {
    private String userId;
    private String deviceId;
    private String deviceToken;
}
