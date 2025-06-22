package com.example.locket_clone.service;

import com.example.locket_clone.entities.Device;

import java.util.List;
import java.util.Set;

public interface DeviceService {
    void updateDeviceToken(String userId, String deviceId, String deviceToken);
    void deleteDeviceToken(String userId, String deviceId);
    List<Device> getAllDevices();
    Set<String> getDeviceTokenByUserIds(List<String> userIds);
}
