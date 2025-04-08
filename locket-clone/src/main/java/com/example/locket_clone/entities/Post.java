package com.example.locket_clone.entities;

import com.example.locket_clone.utils.Constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(Constant.COLLECTION.POST_COLLECTION)
public class Post extends BaseEntity {
    private static final String USER_ID = "user_id";
    private static final String IMAGE_URL = "image_url";
    private static final String REACTION_IDS = "reaction_ids";
    private static final String CAPTION = "caption";
    private static final String FRIEND_IDS = "friend_ids";

    @Field(USER_ID)
    private String userId;

    @Field(IMAGE_URL)
    private String imageURL;

    @Field(REACTION_IDS)
    private List<String> reactionIds = new ArrayList<>();

    @Field(FRIEND_IDS)
    private List<String> friendIds = new ArrayList<>();

    @Field(CAPTION)
    private String caption;
}
