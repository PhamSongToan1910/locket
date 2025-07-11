package com.example.locket_clone.service;

import com.example.locket_clone.entities.LastMessage;
import com.example.locket_clone.entities.Message;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LastMessageService {
    void updateLastMessage(Message message);

    List<LastMessage> getLastMessages(List<String> conversationIds, int skip, int take);

    LastMessage getLastMessageByConversationId(String conversationId);

}