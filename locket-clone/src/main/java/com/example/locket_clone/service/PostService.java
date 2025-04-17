package com.example.locket_clone.service;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.request.AddPostRequest;
import com.example.locket_clone.entities.request.GetPostsRequest;
import com.example.locket_clone.entities.response.GetPostResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    boolean addPost(AddPostRequest addPostRequest, String userId);
    List<GetPostResponse> getPosts(String userId, GetPostsRequest getPostsRequest, Pageable pageable);
    boolean addReactionToPost(Post post, String reactionId);
    Post findbyId(String postId);
    boolean hidePost(Post post, String userId);
    void deletePost(String postId);
}
