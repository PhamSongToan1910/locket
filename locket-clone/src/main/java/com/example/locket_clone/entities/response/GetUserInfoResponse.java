package com.example.locket_clone.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserInfoResponse {
    @JsonProperty("first_name")
    private String firstName;

    private String email;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("avt")
    private String avt;

    @JsonProperty("username")
    private String username;

    @JsonProperty("status")
    private int status;

    @JsonProperty("is_complete")
    private boolean isComplete;

    @JsonProperty("created_at")
    private String createdAt;

}
