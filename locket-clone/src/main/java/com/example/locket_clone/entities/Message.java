package com.example.locket_clone.entities;

import com.example.locket_clone.utils.Constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(Constant.COLLECTION.MESSAGE_COLLECTION)
public class Message extends BaseEntity {

    public static final String CONTENT = "content";
    public static final String CONVERSATION_ID = "conversation_id";
    public static final String USER_SENDER_ID = "user_sender_id";
    public static final String IS_READ = "is_read";
    public static final String POST_ID = "post_id";

    @Field(CONTENT)
    private String content;

    @Field(CONVERSATION_ID)
    private String conversationId;

    @Field(USER_SENDER_ID)
    private String userSender;

    @Field(IS_READ)
    private boolean isRead;

    @Field(POST_ID)
    private String postId;
}
