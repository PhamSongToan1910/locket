package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface ConversationRepository extends MongoRepository<Conversation, String>, CustomConversationRepository {
}
