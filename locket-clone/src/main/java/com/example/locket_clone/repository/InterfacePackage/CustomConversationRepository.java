package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Conversation;

import java.util.List;

public interface CustomConversationRepository {
    Conversation findByUserIds(String userId, String friendId);

    List<Conversation> findByUserId(String userId);
}
