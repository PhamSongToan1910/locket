package com.example.locket_clone.entities.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartResponse {
    private List<String> labels;
    private List<Long> data;
    private Integer sum;
}
