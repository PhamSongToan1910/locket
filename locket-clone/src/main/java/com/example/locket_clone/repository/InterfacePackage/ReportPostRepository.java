package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.ReportPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPostRepository  extends MongoRepository<ReportPost,String> {
}
