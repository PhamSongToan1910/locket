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
@Document(Constant.COLLECTION.SEND_FRIEND_REQUEST_COLLECTION)
public class SendRequestFriend {
    public static final String USER_ID = "user_id";
    public static final String FRIEND_ID = "friend_id";

    @Field(USER_ID)
    private String userId;

    @Field(FRIEND_ID)
    private String friendId;

}
