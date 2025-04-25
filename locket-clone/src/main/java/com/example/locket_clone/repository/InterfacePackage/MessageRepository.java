package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String>, CustomMessageRepository {
}
