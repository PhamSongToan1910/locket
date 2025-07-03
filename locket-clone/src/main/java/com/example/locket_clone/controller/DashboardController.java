package com.example.locket_clone.controller;

import com.example.locket_clone.entities.response.ChartResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.impl.StatisticsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locket-clone/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StatisticsServiceImpl dashboardService;

    @GetMapping("/user-chart")
    public ResponseData<?> getUserChart(
            @RequestParam String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String year
    ) {
        ChartResponse response = dashboardService.getUserChart(type, startDate, endDate, month, year);
        return new ResponseData<>(200, "Success", response);
    }

    @GetMapping("/total-user")
    public ResponseData<Long> getTotalUser() {
        Long totalUser = dashboardService.getTotalUser();
        return new ResponseData<>(200, "Success", totalUser);
    }

    @GetMapping("/report-chart")
    public ResponseData<?> getReportChart(
            @RequestParam String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String year
    ) {
        ChartResponse response = dashboardService.getReportChart(type, startDate, endDate, month, year);
        return new ResponseData<>(200, "Success", response);
    }

}
