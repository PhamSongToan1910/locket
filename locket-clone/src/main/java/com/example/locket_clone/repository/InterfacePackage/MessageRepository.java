package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {
}
