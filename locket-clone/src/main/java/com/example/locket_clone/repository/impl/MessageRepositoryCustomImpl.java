package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.Message;
import com.example.locket_clone.repository.InterfacePackage.MessageRepositoryCustom;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom {

    MongoTemplate mongoTemplate;

    @Override
    public List<Message> getMessageByConversationId(String conversationId, Integer skip, Integer limit) {
        Query query = new Query(Criteria.where(Message.CONVERSATION_ID).is(conversationId));
        query.with(Sort.by(Sort.Direction.DESC, Message.CREATE_AT));
        query.skip(skip);
        query.limit(limit);
        return mongoTemplate.find(query, Message.class);
    }

    @Override
    public long countUnreadMessagesByUserReceiverId(String userId) {
        Query query = new Query(Criteria.where(Message.USER_RECEIVER_ID).is(userId));
        query.addCriteria(Criteria.where(Message.IS_READ).is(false));
        return mongoTemplate.count(query, Message.class);
    }
}
