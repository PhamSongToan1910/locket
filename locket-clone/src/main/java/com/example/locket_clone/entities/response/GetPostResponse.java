package com.example.locket_clone.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPostResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("url")
    private String url;

    @JsonProperty("caption")
    private String caption;

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("friend_avt")
    private String friendAvt;

    @JsonProperty("time")
    private String createTime;

    @JsonProperty("is_friend_post")
    private boolean isFriendPost;

    @JsonProperty("is_friend_reaction")
    private boolean isFriendReaction;
}
