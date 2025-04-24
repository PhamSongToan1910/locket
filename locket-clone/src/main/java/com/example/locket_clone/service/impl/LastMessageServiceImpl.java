package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.LastMessage;
import com.example.locket_clone.entities.Message;
import com.example.locket_clone.repository.InterfacePackage.LastMessageRepository;
import com.example.locket_clone.service.LastMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LastMessageServiceImpl implements LastMessageService {

    private final LastMessageRepository lastMessageRepository;

    @Override
    public void updateLastMessage(Message message) {
        LastMessage lastMessage = lastMessageRepository.findByConversationId(message.getConversationId());
        lastMessage.setMessageId(message.getId().toString());
        lastMessage.setLastModifiedAt(Instant.now());
        lastMessageRepository.save(lastMessage);
    }
}
