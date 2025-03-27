package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User> {
    User findByUsername(String username);
    User findByEmail(String email);
}
