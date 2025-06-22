package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.Device;
import com.example.locket_clone.repository.InterfacePackage.CustomDeviceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomDeviceRepositoryImpl implements CustomDeviceRepository {

    MongoTemplate mongoTemplate;


    @Override
    public Device findByUserIdAndDeviceId(String userId, String deviceId) {
        Query query = new Query(Criteria.where(Device.USER_ID).is(userId).and(Device.DEVICE_ID).is(deviceId));
        return mongoTemplate.findOne(query, Device.class);
    }

    @Override
    public List<Device> getDeviceTokenByUserId(List<String> userIds) {
        Query query = new Query(Criteria.where(Device.USER_ID).in(userIds));
        return mongoTemplate.find(query, Device.class);
    }
}
