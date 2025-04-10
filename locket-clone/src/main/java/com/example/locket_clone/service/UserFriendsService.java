package com.example.locket_clone.service;

import com.example.locket_clone.entities.UserFriends;
import com.example.locket_clone.entities.response.GetFriendResponse;

import java.util.List;

public interface UserFriendsService {
    void addFriend(String userId, String friendId);
    boolean acceptFriend(String userId, String friendId);
    void removeSendRequestFriend(String userId, String friendId);
    int getNumberFriends(String userId);
    List<GetFriendResponse> getAllFriends(String userId);
    Boolean checkIsFriend(String userId, String friendId);
    boolean unFriend(String userId, String friendId);
}
