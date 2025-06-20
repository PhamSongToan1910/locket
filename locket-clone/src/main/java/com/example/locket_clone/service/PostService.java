package com.example.locket_clone.service;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.request.AddPostRequest;
import com.example.locket_clone.entities.request.GetPostsRequest;
import com.example.locket_clone.entities.request.UpdateReportPostByAdmin;
import com.example.locket_clone.entities.response.GetAllPostResponse;
import com.example.locket_clone.entities.response.GetPostResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface PostService {
    Post addPost(AddPostRequest addPostRequest, String userId);
    List<GetPostResponse> getPosts(String userId, GetPostsRequest getPostsRequest, Pageable pageable);
    boolean addReactionToPost(Post post, String reactionId);
    Post findbyId(String postId);
    boolean hidePost(Post post, String userId);
    void deletePost(String postId);
    List<Post> getPostsByIds(Set<String> postIds);
    Post getNewestPostByUserId(String userId);
    List<GetAllPostResponse> getAllPostsByAdmin(Pageable pageable);
    void updatePostByAdmin(UpdateReportPostByAdmin request);
}
