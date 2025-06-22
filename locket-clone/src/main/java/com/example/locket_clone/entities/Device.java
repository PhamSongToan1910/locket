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
@Document(Constant.COLLECTION.DEVICE_COLLECTION)
public class Device extends BaseEntity{
    public static final String USER_ID = "user_id";
    public static final String DEVICE_ID = "device_id";
    public static final String DEVICE_TOKEN = "device_token";

    @Field(USER_ID)
    private String userId;

    @Field(DEVICE_ID)
    private String deviceId;

    @Field(DEVICE_TOKEN)
    private String deviceToken;
}
