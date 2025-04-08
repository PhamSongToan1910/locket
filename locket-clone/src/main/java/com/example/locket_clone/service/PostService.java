package com.example.locket_clone.service;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.request.AddPostRequest;

public interface PostService {
    boolean addPost(AddPostRequest addPostRequest, String userId);
}
