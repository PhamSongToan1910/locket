package com.example.locket_clone.entities;

import com.example.locket_clone.utils.Constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(Constant.COLLECTION.REPORT_POSTS_COLLECTION)
public class ReportPost extends BaseEntity {

    public static final String POST_ID = "post_id";
    public static final String USER_IDS = "user_ids";
    public static final String STATUS = "status";
    public static final String ACTION = "action";

    @Field(POST_ID)
    private String postId;

    @Field(USER_IDS)
    private Set<String> userIds;

    @Field(STATUS)
    private Integer status; //1: skip; 2: pending; 3: delete

    @Field(ACTION)
    private boolean action; //true: delete; false: not_delete
}
