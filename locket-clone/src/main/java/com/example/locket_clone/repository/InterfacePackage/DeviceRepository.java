package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceRepository extends MongoRepository<Device, String>, CustomDeviceRepository {
}
