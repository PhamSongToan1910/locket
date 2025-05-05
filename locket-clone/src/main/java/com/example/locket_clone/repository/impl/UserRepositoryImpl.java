package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.repository.InterfacePackage.UserCustomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserRepositoryImpl implements UserCustomRepository {

    MongoTemplate mongoTemplate;

    @Override
    public List<User> findUserNormal() {
        Query query = new Query(Criteria.where(User.AUTHORITIES).ne("admin"));
        return mongoTemplate.find(query, User.class);
    }
}
