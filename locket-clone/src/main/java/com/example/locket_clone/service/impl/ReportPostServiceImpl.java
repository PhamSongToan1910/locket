package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.ReportPost;
import com.example.locket_clone.repository.InterfacePackage.ReportPostRepository;
import com.example.locket_clone.service.ReportPostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportPostServiceImpl implements ReportPostService {

    ReportPostRepository reportPostRepository;

    @Override
    public List<ReportPost> getReportPosts() {
        return reportPostRepository.findAll();
    }

    @Override
    public void addReportPost(String userId, String postId) {
        ReportPost reportPost = new ReportPost(postId, userId);
        reportPostRepository.save(reportPost);
    }
}
