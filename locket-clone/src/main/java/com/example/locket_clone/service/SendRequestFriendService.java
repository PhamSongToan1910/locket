package com.example.locket_clone.service;

import com.example.locket_clone.entities.SendRequestFriend;

import java.util.Set;

public interface SendRequestFriendService {
    boolean sendRequestFriend(SendRequestFriend sendRequestFriend);
    boolean acceptRequestFriend(String userId, String friendId);
    boolean declineRequestFriend(String userId, String friendId);
    Set<String> getFriendsRequestByUserId(String userId);
    Set<String> getFriendsRequestByFriendId(String userId);
    boolean cancelRequestFriend(String userId, String friendId);
}
