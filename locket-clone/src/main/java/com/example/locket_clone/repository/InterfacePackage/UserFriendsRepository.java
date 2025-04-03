package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.UserFriends;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFriendsRepository extends MongoRepository<UserFriends, String> {
    UserFriends findByUserId(String userId);
}
