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
    public boolean sendRequestFriend(SendRequestFriend sendRequestFriend) {
        SendRequestFriend sendRequestFriendCheck = sendRequestFriendRepository.findOneByUserIdAndFriendId(sendRequestFriend.getUserId(), sendRequestFriend.getFriendId());
        if(Objects.isNull(sendRequestFriendCheck)) {
            sendRequestFriendRepository.save(sendRequestFriend);
            return true;
        }
        return false;
    }

    @Override
    public boolean acceptRequestFriend(String userId, String friendId) {
        SendRequestFriend sendRequestFriendCheck = sendRequestFriendRepository.findOneByUserIdAndFriendId(friendId, userId);
        if(Objects.isNull(sendRequestFriendCheck)) {
            return false;
        }
        sendRequestFriendRepository.delete(sendRequestFriendCheck);
        return true;
    }

    @Override
    public boolean declineRequestFriend(String userId, String friendId) {
        SendRequestFriend sendRequestFriendCheck = sendRequestFriendRepository.findOneByUserIdAndFriendId(friendId, userId);
        if(Objects.isNull(sendRequestFriendCheck)) {
            return false;
        }
        sendRequestFriendRepository.delete(sendRequestFriendCheck);
        return true;
    }

    @Override
    public Set<String> getFriendsRequestByUserId(String userId) {
        List<SendRequestFriend> sendRequestFriendList = sendRequestFriendRepository.findByUserId(userId);
        Set<String> friendsSet = new HashSet<>();
        sendRequestFriendList.forEach(sendRequestFriend -> friendsSet.add(sendRequestFriend.getFriendId()));
        return friendsSet;
    }

    @Override
    public boolean cancelRequestFriend(String userId, String friendId) {
        SendRequestFriend sendRequestFriendCheck = sendRequestFriendRepository.findOneByUserIdAndFriendId(userId, friendId);
        if(Objects.isNull(sendRequestFriendCheck)) {
            return false;
        }
        sendRequestFriendRepository.delete(sendRequestFriendCheck);
        return true;
    }

}
