package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.repository.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String>, UserCustomRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
