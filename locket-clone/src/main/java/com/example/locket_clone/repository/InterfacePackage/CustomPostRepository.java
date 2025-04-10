package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.response.GetPostResponse;

import java.util.List;

public interface CustomPostRepository {
    List<GetPostResponse> GetAllPosts(String userId, int skip, int limit);
}
