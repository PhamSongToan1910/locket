package com.example.locket_clone.entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
public class BaseEntity {
    public static final String _ID = "_id";
    public static final String CREATE_BY = "create_by";
    public static final String CREATE_AT = "create_at";
    public static final String LAST_MODIFIED_BY = "last_modified_by";
    public static final String LAST_MODIFIED_AT = "last_modified_at";
    public static final String IS_DELETE = "is_delete";


    @Field(_ID)
    @Id
    private ObjectId id;

    @Field(CREATE_BY)
    private String createdBy;

    @Field(CREATE_AT)
    private Instant createdAt = Instant.now();

    @Field(LAST_MODIFIED_BY)
    private String lastModifiedBy;

    @Field(LAST_MODIFIED_AT)
    private Instant lastModifiedAt = Instant.now();

    @Field(IS_DELETE)
    private Boolean isDeleted = false;

    public void markAsDelete(){
        this.isDeleted = true;
    }
}
