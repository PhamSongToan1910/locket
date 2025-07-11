package com.example.locket_clone.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("time")
    private String time;
}
