package com.example.locket_clone.entities.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddReactionPost {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("post_id")
    private String postId;

    @JsonProperty("react_type")
    private Integer reactType;
}
