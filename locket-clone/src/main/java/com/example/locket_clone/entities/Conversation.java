package com.example.locket_clone.entities;

import com.example.locket_clone.utils.Constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(Constant.COLLECTION.CONVERSATION_COLLECTION)
public class Conversation extends BaseEntity{
    public static final String USER_IDS = "user_ids";

    @Field(USER_IDS)
    private Set<String> userIds = new HashSet<>();
}
