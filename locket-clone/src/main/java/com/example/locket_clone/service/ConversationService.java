package com.example.locket_clone.service;

import com.example.locket_clone.entities.Conversation;

import java.util.List;

public interface ConversationService {
    Conversation createConversation(String userId, String friendId);

    Conversation getConversationById(String id);

    Conversation getConversationByUserIdAndFriendId(String userId, String friendId);

    List<String> getConversationIdByUserId(String userId);
}
