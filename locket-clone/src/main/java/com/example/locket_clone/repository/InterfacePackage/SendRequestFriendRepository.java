package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.SendRequestFriend;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SendRequestFriendRepository extends MongoRepository<SendRequestFriend, String>, CustomSendRequestFriendRepository {
    SendRequestFriend findOneByUserIdAndFriendId(String userId, String friendId);
    List<SendRequestFriend> findByUserId(String userId);
}
