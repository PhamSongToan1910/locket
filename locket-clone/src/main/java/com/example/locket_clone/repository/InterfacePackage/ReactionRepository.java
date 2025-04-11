package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends MongoRepository<Reaction, String> {
    Reaction findReactionByUserIdAndPostId(String userId, String postId);
}
