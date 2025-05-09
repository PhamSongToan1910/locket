package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.LastMessage;
import com.example.locket_clone.entities.Message;
import com.example.locket_clone.repository.InterfacePackage.LastMessageRepository;
import com.example.locket_clone.repository.InterfacePackage.MessageRepository;
import com.example.locket_clone.service.LastMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LastMessageServiceImpl implements LastMessageService {

    private final LastMessageRepository lastMessageRepository;

    @Override
    public void updateLastMessage(Message message) {
        LastMessage lastMessage = lastMessageRepository.findByConversationId(message.getConversationId());
        if(Objects.nonNull(lastMessage)) {
            lastMessage.setMessageId(message.getId().toString());
            lastMessage.setLastModifiedAt(Instant.now());
            lastMessageRepository.save(lastMessage);
        } else {
            lastMessage = new LastMessage();
            lastMessage.setConversationId(message.getConversationId());
            lastMessage.setMessageId(message.getId().toString());
            lastMessage.setLastModifiedAt(Instant.now());
            lastMessageRepository.save(lastMessage);
        }
    }

    @Override
    public List<LastMessage> getLastMessages(int skip, int take) {
        return lastMessageRepository.getLastMessages(skip, take);
    }

    @Override
    public LastMessage getLastMessageByConversationId(String conversationId) {
        return lastMessageRepository.findByConversationId(conversationId);
    }
}
