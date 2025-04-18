package com.example.locket_clone.entities;

import com.example.locket_clone.utils.Constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(Constant.COLLECTION.UNREAD_POST_COLLECTION)
public class UnreadPost extends BaseEntity{
    public static final String USER_ID = "user_id";
    public static final String POST_IDS = "post_ids";

    @Field(USER_ID)
    private String userId;

    @Field(POST_IDS)
    private LinkedList<String> postIds = new LinkedList<>();
}
