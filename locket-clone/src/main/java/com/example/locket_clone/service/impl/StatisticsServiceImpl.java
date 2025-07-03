package com.example.locket_clone.service.impl;


import com.example.locket_clone.entities.response.ChartResponse;
import com.example.locket_clone.repository.InterfacePackage.ReportPostRepository;
import com.example.locket_clone.repository.InterfacePackage.UserRepository;
import com.google.firebase.remoteconfig.internal.TemplateResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsServiceImpl {

    private UserRepository userRepository;
    private final ReportPostRepository reportPostRepository;

    public ChartResponse getUserChart(String type, String startDate, String endDate, String month, String year) {
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        ZoneId zoneId = ZoneId.systemDefault();
        int sum = 0;
        if ("range".equals(type)) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            LocalDate current = start;

            while (!current.isAfter(end)) {
                Instant startInstant = current.atStartOfDay(zoneId).toInstant();
                Instant endInstant = current.plusDays(1).atStartOfDay(zoneId).toInstant();
                Long count = userRepository.countByCreatedAtBetween(startInstant, endInstant);
                sum += count;
                labels.add(current.toString());
                data.add(count);
                current = current.plusDays(1);
            }

        } else if ("month".equals(type)) {
            YearMonth ym = YearMonth.parse(month);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            // Phân chia theo tuần
            LocalDate currentStart = start;
            int weekIndex = 1;

            while (!currentStart.isAfter(end)) {
                LocalDate currentEnd = currentStart.plusDays(6);
                if (currentEnd.isAfter(end)) currentEnd = end;

                Instant startInstant = currentStart.atStartOfDay(zoneId).toInstant();
                Instant endInstant = currentEnd.plusDays(1).atStartOfDay(zoneId).toInstant();

                Long count = userRepository.countByCreatedAtBetween(startInstant, endInstant);
                sum += count;
                labels.add("Week " + weekIndex);
                data.add(count);

                currentStart = currentEnd.plusDays(1);
                weekIndex++;
            }

        } else if ("year".equals(type)) {
            Year y = Year.parse(year);

            for (int m = 1; m <= 12; m++) {
                YearMonth ym = YearMonth.of(y.getValue(), m);
                LocalDate start = ym.atDay(1);
                LocalDate end = ym.atEndOfMonth();

                Instant startInstant = start.atStartOfDay(zoneId).toInstant();
                Instant endInstant = end.plusDays(1).atStartOfDay(zoneId).toInstant();

                Long count = userRepository.countByCreatedAtBetween(startInstant, endInstant);
                sum += count;
                labels.add(String.format("%02d", m));
                data.add(count);
            }
        }

        return new ChartResponse(labels, data, sum);
    }

    public Long getTotalUser() {
        return userRepository.count();
    }


    public ChartResponse getReportChart(String type, String startDate, String endDate, String month, String year) {
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        ZoneId zoneId = ZoneId.systemDefault();
        int sum = 0;

        if ("range".equals(type)) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            LocalDate current = start;

            while (!current.isAfter(end)) {
                Instant startInstant = current.atStartOfDay(zoneId).toInstant();
                Instant endInstant = current.plusDays(1).atStartOfDay(zoneId).toInstant();

                Long count = reportPostRepository.countByCreatedAtBetween(startInstant, endInstant);
                sum += count;
                labels.add(current.toString());
                data.add(count);
                current = current.plusDays(1);
            }

        } else if ("month".equals(type)) {
            YearMonth ym = YearMonth.parse(month);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            // Phân theo tuần
            LocalDate currentStart = start;
            int weekIndex = 1;

            while (!currentStart.isAfter(end)) {
                LocalDate currentEnd = currentStart.plusDays(6);
                if (currentEnd.isAfter(end)) currentEnd = end;

                Instant startInstant = currentStart.atStartOfDay(zoneId).toInstant();
                Instant endInstant = currentEnd.plusDays(1).atStartOfDay(zoneId).toInstant();

                Long count = reportPostRepository.countByCreatedAtBetween(startInstant, endInstant);
                sum += count;
                labels.add("Tuần " + weekIndex);
                data.add(count);

                currentStart = currentEnd.plusDays(1);
                weekIndex++;
            }

        } else if ("year".equals(type)) {
            Year y = Year.parse(year);

            for (int m = 1; m <= 12; m++) {
                YearMonth ym = YearMonth.of(y.getValue(), m);
                LocalDate start = ym.atDay(1);
                LocalDate end = ym.atEndOfMonth();

                Instant startInstant = start.atStartOfDay(zoneId).toInstant();
                Instant endInstant = end.plusDays(1).atStartOfDay(zoneId).toInstant();

                Long count = reportPostRepository.countByCreatedAtBetween(startInstant, endInstant);
                sum += count;
                labels.add(String.format("%02d", m)); // 01, 02, ..., 12
                data.add(count);
            }
        }
        return new ChartResponse(labels, data, sum);
    }

}


