package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.LastMessage;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomLastMessageRepository {
    List<LastMessage> getLastMessages(List<String> conversationIds, int skip, int take);
}
