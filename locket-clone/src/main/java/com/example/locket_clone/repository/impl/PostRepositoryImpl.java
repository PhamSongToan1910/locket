package com.example.locket_clone.repository.impl;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.repository.InterfacePackage.CustomPostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        query.with(Sort.by(Sort.Direction.DESC, Post.CREATE_AT));
        query.with(pageable);
        return mongoTemplate.find(query, Post.class);
    }

    @Override
    public List<Post> getPostByFriendId(String userId, String friendId, Pageable pageable) {
        Query query= new Query(Criteria.where(Post.USER_ID).is(friendId));
        query.addCriteria(Criteria.where(Post.FRIEND_IDS).in(userId));
        query.addCriteria(Criteria.where(Post.IS_DELETE).is(false));
        query.with(Sort.by(Sort.Direction.DESC, Post.CREATE_AT));
        query.with(pageable);
        return mongoTemplate.find(query, Post.class);
    }

    @Override
    public List<Post> getMyPosts(String userId, Pageable pageable) {
        Query query = new Query(Criteria.where(Post.USER_ID).is(userId));
        query.addCriteria(Criteria.where(Post.IS_DELETE).is(false));
        query.with(Sort.by(Sort.Direction.DESC, Post.CREATE_AT));
        query.with(pageable);
        return mongoTemplate.find(query, Post.class);
    }

    @Override
    public Post getNewestPostByUserId(String userId) {
        Query query = new Query(Criteria.where(Post.FRIEND_IDS).in(userId));
        query.addCriteria(Criteria.where(Post.IS_DELETE).is(false));
        query.with(Sort.by(Sort.Direction.DESC, Post.CREATE_AT));
        query.limit(1);
        return mongoTemplate.findOne(query, Post.class);
    }

    @Override
    public List<Post> getAllPostsByAdmin(Pageable pageable) {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, Post.CREATE_AT));
        query.with(pageable);
        return mongoTemplate.find(query, Post.class);
    }
}
