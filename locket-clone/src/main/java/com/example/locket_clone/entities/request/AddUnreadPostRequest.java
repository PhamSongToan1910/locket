package com.example.locket_clone.entities.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUnreadPostRequest {
    private List<String> userIds;
    private String postId;
}
