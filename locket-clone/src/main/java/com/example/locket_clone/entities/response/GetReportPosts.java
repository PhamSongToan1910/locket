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
import java.util.Set;

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

    @JsonProperty("owner_id")
    private String onwerId;

    @JsonProperty("create_at")
    private String createdAt;

    @JsonProperty("status")
    private String status;

    @JsonProperty("action")
    private String action;

    @JsonProperty("user_ids")
    private Set<String> userIds;

    @JsonProperty("count")
    private int count;

    public void convertCreateAtInstantToString(Instant instant) {
        String timeString = instant.toString();
        String builder = timeString.split("T")[0] +
                " " +
                timeString.split("T")[1].substring(0,8);
        this.createdAt = builder;
    }
}
