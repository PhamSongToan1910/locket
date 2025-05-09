package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Conversation;
import com.example.locket_clone.repository.InterfacePackage.ConversationRepository;
import com.example.locket_clone.service.ConversationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationServiceImpl implements ConversationService {

    ConversationRepository conversationRepository;

    @Override
    public void createConversation(String userId, String friendId) {
        Conversation conversation = new Conversation();
        conversation.getUserIds().add(userId);
        conversation.getUserIds().add(friendId);
        conversationRepository.save(conversation);
    }

    @Override
    public Conversation getConversationById(String id) {
        return conversationRepository.findById(id).orElse(null);
    }

    @Override
    public Conversation getConversationByUserIdAndFriendId(String userId, String friendId) {
        return conversationRepository.findByUserIds(userId, friendId);
    }
}
