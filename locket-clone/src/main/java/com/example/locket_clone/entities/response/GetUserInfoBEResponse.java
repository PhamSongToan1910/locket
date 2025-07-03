package com.example.locket_clone.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserInfoBEResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("create_at")
    private String createAt;

    @JsonProperty("avt")
    private String avt;

    @JsonProperty("is_delete")
    private String isDeleted;

    public void convertCreateAtInstantToString(Instant instant) {
        String timeString = instant.toString();
        String builder = timeString.split("T")[0] +
                " " +
                timeString.split("T")[1].substring(0,8);
        this.createAt = builder;
    }
}
