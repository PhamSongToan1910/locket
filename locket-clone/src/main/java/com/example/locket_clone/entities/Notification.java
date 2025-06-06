package com.example.locket_clone.entities;

import com.example.locket_clone.utils.Constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(Constant.COLLECTION.NOTIFICATION_COLLECTION)
public class Notification extends BaseEntity{
    public static final String POST_ID = "post_id";
    public static final String TYPE = "type";
    public static final String USER_ID = "user_id";

    @Field(POST_ID)
    private String postId;

    @Field(TYPE)
    private int type;

    @Field(USER_ID)
    private String userId;
}
