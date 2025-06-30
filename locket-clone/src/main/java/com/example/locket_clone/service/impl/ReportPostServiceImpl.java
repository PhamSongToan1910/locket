package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.ReportPost;
import com.example.locket_clone.entities.request.UpdateReportPostByAdmin;
import com.example.locket_clone.repository.InterfacePackage.ReportPostRepository;
import com.example.locket_clone.service.ReportPostService;
import com.example.locket_clone.utils.Constant.Constant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportPostServiceImpl implements ReportPostService {

    ReportPostRepository reportPostRepository;

    @Override
    public List<ReportPost> getReportPosts(int kind, Pageable pageable) {
        return reportPostRepository.findByStatusOrderByCreatedAtDesc(kind, pageable).stream().toList();
    }

    @Override
    public void addReportPost(String userId, String postId) {
        ReportPost reportPost = reportPostRepository.findReportPostByPostId(postId);
        if(reportPost != null) {
            reportPost.getUserIds().add(userId);
            reportPostRepository.save(reportPost);
        } else {
            Set<String> setUserIds = new HashSet<>();
            setUserIds.add(userId);
            ReportPost saveRepotrPost = new ReportPost(postId, setUserIds, Constant.STATUS_REPORT_POST.PENDING, false);
            reportPostRepository.save(saveRepotrPost);
        }
    }

    @Override
    public void updateStatusAndActionReportPostByAdmin(UpdateReportPostByAdmin request) {
        ReportPost reportPost = reportPostRepository.findReportPostByPostId(request.getPostId());
        reportPost.setStatus(request.getStatus());
        reportPost.setAction(request.isAction());
        reportPost.setLastModifiedBy(request.getUserId());
        reportPostRepository.save(reportPost);
    }


}
