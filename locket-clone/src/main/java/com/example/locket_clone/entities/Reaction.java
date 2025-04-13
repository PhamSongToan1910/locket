package com.example.locket_clone.entities;

import com.example.locket_clone.utils.Constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedList;

@NoArgsConstructor
@AllArgsConstructor
@Document(Constant.COLLECTION.REACTION_COLLECTION)
public class Reaction extends BaseEntity {

    public final String USER_ID = "user_id";
    public final String POST_ID = "post_id";
    public final String ICONS = "icons";

    @Field(USER_ID)
    private String userId;

    @Field(POST_ID)
    private String postId;

    @Field(ICONS)
    private LinkedList<Integer> icons; //Tym = 1; Fire = 2; Haha = 3; Cry = 4;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LinkedList<Integer> getIcons() {
        return icons;
    }

    public void setIcons(LinkedList<Integer> icons) {
        this.icons = icons;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
