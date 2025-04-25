package com.example.locket_clone.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListConversationResponse {
    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("fullname")
    private String name;

    @JsonProperty("avt")
    private String avt;

    @JsonProperty("content")
    private String content;

    @JsonProperty("is_read")
    private boolean isRead;

    @JsonProperty("create_at")
    private Instant createdAt;
}
