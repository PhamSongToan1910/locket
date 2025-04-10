package com.example.locket_clone.service;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddUserRequest;
import com.example.locket_clone.entities.request.UpdateUserInfoRequest;
import com.example.locket_clone.entities.request.UpdateUserInforV2Request;
import com.example.locket_clone.entities.response.GetFriendResponse;
import com.example.locket_clone.entities.response.SearchFriendByUsernameResponse;

public interface UserService {
    User insertUser(AddUserRequest user);

    User findUserByEmail(String email);

    User findUserByUsername(String username);

    User findUserById(String id);

    Boolean updateUser(UpdateUserInfoRequest user, String userId);

    Boolean updateUserV2(UpdateUserInforV2Request updateUserInforV2, String userId);

    Boolean updateAvt(String userId, String avtPath);

    SearchFriendByUsernameResponse searchByUsername(String username);
}
