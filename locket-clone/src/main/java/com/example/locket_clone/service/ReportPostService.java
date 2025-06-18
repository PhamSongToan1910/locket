package com.example.locket_clone.service;

import com.example.locket_clone.entities.ReportPost;
import com.example.locket_clone.entities.request.UpdateReportPostByAdmin;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportPostService {
    List<ReportPost> getReportPosts(int kind, Pageable pageable);
    void addReportPost(String userId, String postId);
    void updateStatusAndActionReportPostByAdmin(UpdateReportPostByAdmin request);
}
