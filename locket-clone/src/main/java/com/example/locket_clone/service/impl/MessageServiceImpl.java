package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Message;
import com.example.locket_clone.repository.InterfacePackage.MessageRepository;
import com.example.locket_clone.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    public List<Message> getMessagesByConversationId(String conversationId, Pageable pageable) {
        return messageRepository.findByConversationIdOrderByConversationIdDesc(conversationId, pageable);
    }

    @Override
    public Message getMessageById(String messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    @Override
    public long countUnreadMessageByUserReceiverId(String userId) {
        return messageRepository.countUnreadMessageByUserReceiverId(userId);
    }

    @Override
    public void updateReadStatus(String messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if(message != null) {
            message.setRead(true);
            message.setLastModifiedAt(Instant.now());
            messageRepository.save(message);
        }
    }
}
