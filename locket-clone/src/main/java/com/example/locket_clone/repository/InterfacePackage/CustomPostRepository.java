package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPostRepository {
    List<Post> GetAllPosts(String userId, Pageable pageable);

    List<Post> getPostByFriendId(String userId, String friendId, Pageable pageable);

    List<Post> getMyPosts(String userId, Pageable pageable);

    Post getNewestPostByUserId(String userId);

    List<Post> getAllPostsByAdmin(Pageable pageable);
}
