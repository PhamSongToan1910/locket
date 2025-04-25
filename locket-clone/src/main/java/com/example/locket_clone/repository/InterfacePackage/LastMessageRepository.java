package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.LastMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LastMessageRepository extends MongoRepository<LastMessage, String>, CustomLastMessageRepository {
    LastMessage findByConversationId(String conversationId);
}
