package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.SendRequestFriend;
import com.example.locket_clone.repository.InterfacePackage.CustomSendRequestFriendRepository;
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
public class SendRequestRepositoryImpl implements CustomSendRequestFriendRepository {

    MongoTemplate mongoTemplate;

    @Override
    public List<SendRequestFriend> findByUserId(String userId) {
        Query query = new Query(Criteria.where(SendRequestFriend.USER_ID).is(userId));
        return mongoTemplate.find(query, SendRequestFriend.class);
    }
}
