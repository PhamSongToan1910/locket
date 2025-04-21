package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Message;
import com.example.locket_clone.repository.InterfacePackage.MessageRepository;
import com.example.locket_clone.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageServiceImpl implements MessageService {

    MessageRepository messageRepository;

    @Override
    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    @Override
    public List<Message> getMessagesByConversationId(String conversationId) {
        return messageRepository.findByConversationIdOrderByConversationIdDesc(conversationId);
    }
}
