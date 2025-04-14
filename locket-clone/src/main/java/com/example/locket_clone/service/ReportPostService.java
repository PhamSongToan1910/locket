package com.example.locket_clone.service;

import com.example.locket_clone.entities.ReportPost;

import java.util.List;

public interface ReportPostService {
    List<ReportPost> getReportPosts();
    void addReportPost(String userId, String postId);
}
