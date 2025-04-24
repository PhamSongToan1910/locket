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
@Document(Constant.COLLECTION.LAST_MESSAGE_COLLECTION)
public class LastMessage extends BaseEntity{

    public static final String MESSSAGE_ID = "message_id";
    public static final String CONVERSATION_ID = "conversation_id";

    @Field(value = MESSSAGE_ID)
    private String messageId;

    @Field(value = CONVERSATION_ID)
    private String conversationId;
}
