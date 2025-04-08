package com.example.locket_clone.entities.request;

import com.example.locket_clone.utils.Constant.Constant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPostRequest {

    @JsonProperty("caption")
    private String caption;

    @JsonProperty("type")
    private Integer type;

    @JsonProperty("friend_ids")
    private List<String> friendIds;

    @JsonProperty("image_url")
    private String imageURL;

    public boolean validateRequest() {
        if(!StringUtils.hasLength(this.caption) || Objects.isNull(this.type)) {
            return false;
        }
        if(this.type == Constant.TYPE_ADD_POST.PROTECTED) {
            return !CollectionUtils.isEmpty(friendIds);
        }
        return true;
    }
}
