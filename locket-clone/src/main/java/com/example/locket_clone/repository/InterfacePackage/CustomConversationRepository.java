package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Conversation;

public interface CustomConversationRepository {
    Conversation findByUserIds(String userId, String friendId);
}
