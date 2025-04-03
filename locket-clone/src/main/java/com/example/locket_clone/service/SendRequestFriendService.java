package com.example.locket_clone.service;

import com.example.locket_clone.entities.SendRequestFriend;

public interface SendRequestFriendService {
    void sendRequestFriend(SendRequestFriend sendRequestFriend);
    void acceptRequestFriend(String userId, String friendId);
    void declineRequestFriend(String userId, String friendId);
}
