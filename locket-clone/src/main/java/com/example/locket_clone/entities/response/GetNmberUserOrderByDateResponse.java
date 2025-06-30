package com.example.locket_clone.entities.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetNmberUserOrderByDateResponse {
    private String date;
    private int count;
}
