package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.repository.AbstractRepository;
import com.example.locket_clone.repository.InterfacePackage.UserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl extends AbstractRepository<User> implements UserRepository {

    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }

    @Override
    public User findByUsername(String username) {
        Query query = new Query(Criteria.where(User.USERNAME).is(username));
        return mongoTemplate.findOne(query, User.class);
    }

    @Override
    public User findByEmail(String email) {
        Query query = new Query(Criteria.where(User.EMAIL).is(email));
        return mongoTemplate.findOne(query, User.class);
    }
}
