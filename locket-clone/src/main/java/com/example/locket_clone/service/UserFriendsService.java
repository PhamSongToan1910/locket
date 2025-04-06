package com.example.locket_clone.service;

import com.example.locket_clone.entities.UserFriends;

import java.util.List;

public interface UserFriendsService {
    void addFriend(String userId, String friendId);
    void removeFriend(String userId, String friendId);
    int getNumberFriends(String userId);
    UserFriends getAllFriends(String userId);
}
