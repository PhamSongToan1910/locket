package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<Post, String>, CustomPostRepository {
}
