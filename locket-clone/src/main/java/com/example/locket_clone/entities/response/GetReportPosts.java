package com.example.locket_clone.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetReportPosts {
    @JsonProperty("post_id")
    private String postId;

    @JsonProperty("caption")
    private String caption;

    @JsonProperty("image_url")
    private String imageURL;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("create_at")
    private Instant createdAt;
}
