package com.example.locket_clone.service;

import com.example.locket_clone.entities.Conversation;

public interface ConversationService {
    void createConversation(String userId, String friendId);

    Conversation getConversationById(String id);
}
