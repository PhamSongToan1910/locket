package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.repository.InterfacePackage.CustomPostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PostRepositoryImpl implements CustomPostRepository {

    MongoTemplate mongoTemplate;

    @Override
    public List<Post> GetAllPosts(String userId, Pageable pageable) {
        Query query = new Query(Criteria.where(Post.FRIEND_IDS).in(userId));
        query.addCriteria(Criteria.where(Post.IS_DELETE).is(false));
        query.with(pageable);
        return mongoTemplate.find(query, Post.class);
    }
}
