package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPostRepository {
    List<Post> GetAllPosts(String userId, Pageable pageable);
}
