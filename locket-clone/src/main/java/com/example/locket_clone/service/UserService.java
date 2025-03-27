package com.example.locket_clone.service;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddUserRequest;

public interface UserService {
    void insertUser(AddUserRequest user);

    User findUserByEmail(String email);

    User findUserByUsername(String username);
}
