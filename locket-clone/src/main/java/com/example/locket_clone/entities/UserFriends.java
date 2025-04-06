package com.example.locket_clone.entities;

import com.example.locket_clone.utils.Constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(Constant.COLLECTION.USER_FRIENDS_COLLECTION)
public class UserFriends extends BaseEntity {

    public static final String USER_ID = "user_id";
    public static final String FRIEND_IDS = "friend_ids";

    @Field(USER_ID)
    private String userId;

    @Field(FRIEND_IDS)
    private Set<String> friendIds = new HashSet<>();
}
