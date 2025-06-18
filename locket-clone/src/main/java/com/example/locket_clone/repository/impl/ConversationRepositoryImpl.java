package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.Conversation;
import com.example.locket_clone.repository.InterfacePackage.CustomConversationRepository;
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
public class ConversationRepositoryImpl implements CustomConversationRepository {

    MongoTemplate mongoTemplate;

    @Override
    public Conversation findByUserIds(String userId, String friendId) {
        Query query = new Query(Criteria.where(Conversation.USER_IDS).all(userId, friendId));
        return mongoTemplate.findOne(query, Conversation.class);
    }

    @Override
    public List<Conversation> findByUserId(String userId) {
        Query query = new Query(Criteria.where(Conversation.USER_IDS).is(userId));
        return mongoTemplate.find(query, Conversation.class);
    }
}
