package com.example.locket_clone.service;

public interface UserFriendsService {
    void addFriend(String userId, String friendId);
    void removeFriend(String userId, String friendId);
    int getNumberFriends(String userId);
}
