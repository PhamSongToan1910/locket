package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.repository.InterfacePackage.UserCustomRepository;
import com.example.locket_clone.repository.InterfacePackage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository {
}
