package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Message;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomMessageRepository {
    List<Message> findByConversationIdOrderByConversationIdDesc(String conversationId, int skip, int take);

    long countUnreadMessageByUserReceiverId(String userId);

}
