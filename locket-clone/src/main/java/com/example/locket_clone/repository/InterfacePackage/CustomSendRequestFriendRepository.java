package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.SendRequestFriend;

import java.util.List;

public interface CustomSendRequestFriendRepository {
    List<SendRequestFriend> findByUserId(String userId);
}
