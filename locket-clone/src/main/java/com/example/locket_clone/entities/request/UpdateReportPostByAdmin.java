package com.example.locket_clone.entities.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportPostByAdmin {
    private String postId;
    private String userId;
    private int status;
    private boolean action;
}
