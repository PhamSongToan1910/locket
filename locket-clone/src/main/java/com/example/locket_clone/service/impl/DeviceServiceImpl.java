package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Device;
import com.example.locket_clone.repository.InterfacePackage.DeviceRepository;
import com.example.locket_clone.service.DeviceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeviceServiceImpl implements DeviceService {


    DeviceRepository deviceRepository;

    @Override
    public void updateDeviceToken(String userId, String deviceId, String deviceToken) {
        Device device = deviceRepository.findByUserIdAndDeviceId(userId, deviceId);
        if(Objects.nonNull(device)) {
            device.setDeviceToken(deviceToken);
            deviceRepository.save(device);
        } else {
            Device newDevice = new Device(userId, deviceId, deviceToken);
            deviceRepository.save(newDevice);
        }
    }

    @Override
    public void deleteDeviceToken(String userId, String deviceId) {
        Device device = deviceRepository.findByUserIdAndDeviceId(userId, deviceId);
        if(Objects.nonNull(device)) {
            deviceRepository.delete(device);
        }
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public Set<String> getDeviceTokenByUserIds(List<String> userIds) {
        List<Device> devices = deviceRepository.getDeviceTokenByUserId(userIds);
        return devices.stream().map(Device::getDeviceToken).collect(Collectors.toSet());
    }
}
