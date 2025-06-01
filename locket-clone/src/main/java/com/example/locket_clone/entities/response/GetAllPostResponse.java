package com.example.locket_clone.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllPostResponse {
    @JsonProperty("post_id")
    private String id;

    @JsonProperty("caption")
    private String caption;

    @JsonProperty("image_url")
    private String imageURL;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("create_time")
    private String createTime;
}
