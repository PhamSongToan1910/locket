package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.UserFriends;
import com.example.locket_clone.repository.InterfacePackage.UserFriendsRepository;
import com.example.locket_clone.service.UserFriendsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFriendsServiceImpl implements UserFriendsService {

    UserFriendsRepository userFriendsRepository;

    @Override
    public void addFriend(String userId, String friendId) {
        UserFriends userFriends = userFriendsRepository.findByUserId(userId);
        userFriends.getFriendIds().add(friendId);
        userFriendsRepository.save(userFriends);
    }

    @Override
    public void removeFriend(String userId, String friendId) {
        UserFriends userFriends = userFriendsRepository.findByUserId(userId);
        userFriends.getFriendIds().remove(friendId);
        userFriendsRepository.save(userFriends);
    }

    @Override
    public int getNumberFriends(String userId) {
        UserFriends userFriends = userFriendsRepository.findByUserId(userId);
        return userFriends.getFriendIds().size();
    }
}
