package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.SendRequestFriend;
import com.example.locket_clone.repository.InterfacePackage.SendRequestFriendRepository;
import com.example.locket_clone.service.SendRequestFriendService;
import com.example.locket_clone.utils.Constant.Constant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SendRequestFriendServiceImpl implements SendRequestFriendService {

    SendRequestFriendRepository sendRequestFriendRepository;

    @Override
    public void sendRequestFriend(SendRequestFriend sendRequestFriend) {
        SendRequestFriend sendRequestFriendCheck = sendRequestFriendRepository.findOneByUserIdAndFriendId(sendRequestFriend.getUserId(), sendRequestFriend.getFriendId());
        if(Objects.isNull(sendRequestFriendCheck)) {
            sendRequestFriendRepository.save(sendRequestFriend);
        }
    }

    @Override
    public void acceptRequestFriend(String userId, String friendId) {
        sendRequestFriendRepository.deleteByUserIdAndFriendId(friendId, userId);
    }

    @Override
    public void declineRequestFriend(String userId, String friendId) {
        sendRequestFriendRepository.deleteByUserIdAndFriendId(friendId, userId);
    }

    @Override
    public Set<String> getFriendsRequestByUserId(String userId) {
        List<SendRequestFriend> sendRequestFriendList = sendRequestFriendRepository.findByUserId(userId);
        Set<String> friendsSet = new HashSet<>();
        sendRequestFriendList.forEach(sendRequestFriend -> friendsSet.add(sendRequestFriend.getFriendId()));
        return friendsSet;
    }

}
