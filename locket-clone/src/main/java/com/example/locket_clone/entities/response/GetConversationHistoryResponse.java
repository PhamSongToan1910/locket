package com.example.locket_clone.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetConversationHistoryResponse {

    @JsonProperty("content")
    private String content;

    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("user_sender_id")
    private String userSenderId;

    @JsonProperty("user_receiver_id")
    private String userReceiverId;

    @JsonProperty("is_read")
    private boolean isRead;

    @JsonProperty("post_url")
    private String postURL;

    @JsonProperty("id")
    private String id;

    @JsonProperty("create_by")
    private String createdBy;

    @JsonProperty("create_at")
    private Instant createdAt;

    @JsonProperty("last_modified_by")
    private String lastModifiedBy;

    @JsonProperty("last_modified_at")
    private Instant lastModifiedAt;

    @JsonProperty("is_delete")
    private Boolean isDeleted;
}
