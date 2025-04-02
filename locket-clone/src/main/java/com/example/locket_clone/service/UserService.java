package com.example.locket_clone.service;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddUserRequest;
import com.example.locket_clone.entities.request.UpdateUserInfoRequest;

public interface UserService {
    String insertUser(AddUserRequest user);

    User findUserByEmail(String email);

    User findUserByUsername(String username);

    User findUserById(String id);

    Boolean updateUser(UpdateUserInfoRequest user, String userId);
}
