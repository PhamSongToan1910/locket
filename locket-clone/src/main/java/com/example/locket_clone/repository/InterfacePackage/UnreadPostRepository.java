package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.UnreadPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnreadPostRepository extends MongoRepository<UnreadPost, String> {
    UnreadPost findUnreadPostByUserId(String userId);
}
