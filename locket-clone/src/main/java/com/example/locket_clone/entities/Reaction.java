package com.example.locket_clone.entities;

import com.example.locket_clone.utils.Constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedList;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(Constant.COLLECTION.REACTION_COLLECTION)
public class Reaction extends BaseEntity {

    public static final String USER_ID = "user_id";
    public static final String POST_ID = "post_id";
    public static final String ICONS = "icons";

    @Field(USER_ID)
    private String userId;

    @Field(POST_ID)
    private String postId;

    @Field(ICONS)
    private LinkedList<Integer> icons = new LinkedList<>(); //Tym = 1; Fire = 2; Haha = 3; Cry = 4;

}
