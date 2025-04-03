package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.SendRequestFriend;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SendRequestFriendRepository extends MongoRepository<SendRequestFriend, String> {
    SendRequestFriend findOneByUserIdAndFriendId(String userId, String friendId);
}
