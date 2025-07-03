package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.ReportPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReportPostRepository  extends MongoRepository<ReportPost,String> {
    List<ReportPost> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);
    ReportPost findReportPostByPostId(String postId);
    Long countByCreatedAtBetween(Instant startInstant, Instant endInstant);
}
