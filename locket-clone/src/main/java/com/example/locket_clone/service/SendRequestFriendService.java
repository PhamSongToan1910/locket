package com.example.locket_clone.service;

import com.example.locket_clone.entities.SendRequestFriend;

import java.util.Set;

public interface SendRequestFriendService {
    void sendRequestFriend(SendRequestFriend sendRequestFriend);
    void acceptRequestFriend(String userId, String friendId);
    void declineRequestFriend(String userId, String friendId);
    Set<String> getFriendsRequestByUserId(String userId);
}
