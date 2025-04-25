package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.Message;
import com.example.locket_clone.repository.InterfacePackage.CustomMessageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomMessageRepositoryImpl implements CustomMessageRepository {

    MongoTemplate mongoTemplate;

    @Override
    public List<Message> findByConversationIdOrderByConversationIdDesc(String conversationId, Pageable pageable) {
        Query query = new Query(Criteria.where(Message.CONVERSATION_ID).is(conversationId));
        query.with(Sort.by(Sort.Direction.DESC, Message.CREATE_AT));
        query.with(pageable);
        return mongoTemplate.find(query, Message.class);
    }
}
