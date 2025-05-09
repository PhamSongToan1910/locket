package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.LastMessage;
import com.example.locket_clone.repository.InterfacePackage.CustomLastMessageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LastMessageRepositoryImpl implements CustomLastMessageRepository {

    MongoTemplate mongoTemplate;

    @Override
    public List<LastMessage> getLastMessages(int skip, int take) {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, LastMessage.LAST_MODIFIED_AT));
        query.skip(skip);
        query.limit(take);
        return mongoTemplate.find(query, LastMessage.class);
    }
}
