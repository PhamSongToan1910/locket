package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String>, RoleCustomRepository {
    Role findByName(String name);
}
