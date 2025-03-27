package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.Role;
import com.example.locket_clone.repository.AbstractRepository;
import com.example.locket_clone.repository.InterfacePackage.RoleRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryImpl extends AbstractRepository<Role> implements RoleRepository {
    public RoleRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }
}
