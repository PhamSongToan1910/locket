package com.example.locket_clone.entities.request;

import lombok.Data;

import java.time.Instant;

@Data
public class SearchUserRequest {
    private String keyword;
    private String value;
    private String sortBy = "createdAt";
    private String sorDir = "desc"; // Default sort direction is descending
    private Instant startDate;
    private Instant endDate;
    private int page = 0; // Default page number
    private int size = 10; // Default page size
}
