package com.example.locket_clone.entities.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyPostRequest {
    @JsonProperty("content")
    private String content;

    @JsonProperty("post_url")
    private String postURL;

    @JsonProperty("user_receiver")
    private String userId;
}
