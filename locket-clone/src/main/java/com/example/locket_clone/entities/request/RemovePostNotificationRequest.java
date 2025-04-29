package com.example.locket_clone.entities.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemovePostNotificationRequest {
    private String userId;
    private String title;
    private String caption;
    private String imageURL;
}
