package com.example.locket_clone.entities.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFriendResponse {
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String avt;
}
