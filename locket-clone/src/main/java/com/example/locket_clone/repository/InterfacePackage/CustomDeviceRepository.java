package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Device;

import java.util.List;

public interface CustomDeviceRepository {
    Device findByUserIdAndDeviceId(String userId, String deviceId);
    List<Device> getDeviceTokenByUserId(List<String> userId);
}
