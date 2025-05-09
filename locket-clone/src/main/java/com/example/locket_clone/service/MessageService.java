package com.example.locket_clone.service;

import com.example.locket_clone.entities.Message;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {
    void saveMessage(Message message);

    List<Message> getMessagesByConversationId(String conversationId, Pageable pageable);

    Message getMessageById(String messageId);

    long countUnreadMessageByUserReceiverId(String userId);
}
