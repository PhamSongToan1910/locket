package com.example.locket_clone.entities.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPostsRequest {
    @JsonProperty("page")
    private int page = 0;

    @JsonProperty("size")
    private int size = 10;

    @JsonProperty("type")
    private int type;

    @JsonProperty("friend_id")
    private String friendId;

    public boolean validateRequest() {
        if(this.type == 1) {
            return StringUtils.hasText(this.friendId);
        }
        return true;
    }
}
