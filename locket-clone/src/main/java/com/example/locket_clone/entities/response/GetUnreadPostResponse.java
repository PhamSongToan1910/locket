package com.example.locket_clone.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUnreadPostResponse {
    @JsonProperty("post_id")
    private String id;

    @JsonProperty("image_url")
    private String imageURL;

    @JsonProperty("caption")
    private String caption;

    @JsonProperty("friend_avt")
    private String friendAvt;

    @JsonProperty("unread_count")
    private int unreadCount;
}
