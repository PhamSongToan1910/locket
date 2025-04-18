package com.example.locket_clone.service;

import com.example.locket_clone.entities.UnreadPost;

import java.util.List;

public interface UnreadPostService {
    boolean addUnreadPost(String userId, String postId);

    void deleteUnreadPost(String userId);

    UnreadPost getUnreadPosts(String userId);
}
