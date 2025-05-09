package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Message;

import java.util.List;

public interface MessageRepositoryCustom {
    List<Message> getMessageByConversationId(String conversationId, Integer skip, Integer limit);

    long countUnreadMessagesByUserReceiverId(String userId);
}
