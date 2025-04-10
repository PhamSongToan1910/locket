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
    private String firstname;

    @JsonProperty("last_name")
    private String lastname;

    @JsonProperty("avt")
    private String avt;

    @JsonProperty("username")
    private String username;

    @JsonProperty("status")
    private int status;

    @JsonProperty("is_complete")
    private boolean isComplete;
}
