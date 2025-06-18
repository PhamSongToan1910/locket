package com.example.locket_clone.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetReportPosts {
    @JsonProperty("post_id")
    private String postId;

    @JsonProperty("caption")
    private String caption;

    @JsonProperty("image_url")
    private String imageURL;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("owner_id")
    private String onwerId;

    @JsonProperty("create_at")
    private String createdAt;

    @JsonProperty("status")
    private String status;

    @JsonProperty("action")
    private String action;

    public void convertCreateAtInstantToString(Instant instant) {
        String timeString = instant.toString();
        String builder = timeString.split("T")[0] +
                " " +
                timeString.split("T")[1].substring(0,8);
        this.createdAt = builder;
    }
}
