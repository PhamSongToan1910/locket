package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.LastMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastMessageRepository extends MongoRepository<LastMessage, String> {
    LastMessage findByConversationId(String conversationId);
}
