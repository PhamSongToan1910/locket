package com.example.locket_clone.service;

import com.example.locket_clone.entities.Message;

import java.util.List;

public interface MessageService {
    void saveMessage(Message message);

    List<Message> getMessagesByConversationId(String conversationId);
}
