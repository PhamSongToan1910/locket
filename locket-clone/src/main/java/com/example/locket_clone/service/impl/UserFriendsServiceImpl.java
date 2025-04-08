package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.UserFriends;
import com.example.locket_clone.entities.response.GetFriendResponse;
import com.example.locket_clone.repository.InterfacePackage.UserFriendsRepository;
import com.example.locket_clone.service.SendRequestFriendService;
import com.example.locket_clone.service.UserFriendsService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFriendsServiceImpl implements UserFriendsService {

    UserFriendsRepository userFriendsRepository;
    SendRequestFriendService sendRequestFriendService;
    UserService userService;

    @Override
    public void addFriend(String userId, String friendId) {
        UserFriends userFriends = userFriendsRepository.findByUserId(userId);
        if(Objects.isNull(userFriends)) {
            userFriends = new UserFriends();
            userFriends.setUserId(userId);
        }
        userFriends.getFriendIds().add(friendId);
        userFriendsRepository.save(userFriends);
    }

    @Override
    public void removeSendRequestFriend(String userId, String friendId) {
        UserFriends userFriends = userFriendsRepository.findByUserId(friendId);
        userFriends.getFriendIds().remove(userId);
        userFriendsRepository.save(userFriends);
    }

    @Override
    public int getNumberFriends(String userId) {
        UserFriends userFriends = userFriendsRepository.findByUserId(userId);
        return userFriends.getFriendIds().size();
    }

    @Override
    public List<GetFriendResponse> getAllFriends(String userId) {
        UserFriends userFriends = userFriendsRepository.findByUserId(userId);
        if(Objects.isNull(userFriends) || CollectionUtils.isEmpty(userFriends.getFriendIds())) {
            return new ArrayList<>();
        }
        Set<String> sendRequestFriend = sendRequestFriendService.getFriendsRequestByUserId(userId);
        return userFriends.getFriendIds().stream()
                .filter(id -> !sendRequestFriend.contains(id))
                .map(userService::findUserById)
                .filter(Objects::nonNull)
                .map(user -> {
                    GetFriendResponse responseObj = new GetFriendResponse();
                    ModelMapperUtils.toObject(user, responseObj);
                    return responseObj;
                })
                .toList();
    }

}
