package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.UnreadPost;
import com.example.locket_clone.repository.InterfacePackage.UnreadPostRepository;
import com.example.locket_clone.service.UnreadPostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UnreadPostServiceImpl implements UnreadPostService {

    UnreadPostRepository unreadPostRepository;

    @Override
    public boolean addUnreadPost(String userId, String postId) {
        UnreadPost unreadPost = unreadPostRepository.findUnreadPostByUserId(userId);
        if (unreadPost == null) {
            unreadPost = new UnreadPost();
            unreadPost.setUserId(userId);
            unreadPost.getPostIds().add(postId);
            unreadPostRepository.save(unreadPost);
            return true;
        }
        unreadPost.getPostIds().add(postId);
        unreadPostRepository.save(unreadPost);
        return true;
    }

    @Override
    public void deleteUnreadPost(String userId) {
        UnreadPost unreadPost = unreadPostRepository.findUnreadPostByUserId(userId);
        if(unreadPost != null) {
            unreadPost.getPostIds().clear();
            unreadPostRepository.save(unreadPost);
        }
    }

    @Override
    public UnreadPost getUnreadPosts(String userId) {
        return unreadPostRepository.findUnreadPostByUserId(userId);
    }
}
