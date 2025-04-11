package com.example.locket_clone.service;

import com.example.locket_clone.entities.request.AddPostRequest;
import com.example.locket_clone.entities.response.GetPostResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    boolean addPost(AddPostRequest addPostRequest, String userId);
    List<GetPostResponse> getPosts(String userId, Pageable pageable);
    boolean addReactionToPost(String postId, String reactionId);
}
