package com.example.locket_clone.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetChatHistoryResponse {
    @JsonProperty("content")
    private String content;

    @JsonProperty("is_my_owner_message")
    private boolean isMyOwnerMessage;

    @JsonProperty("create_at")
    private Instant createAt;
}
