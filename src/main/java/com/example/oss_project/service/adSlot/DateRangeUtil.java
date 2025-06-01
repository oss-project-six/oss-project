package com.example.oss_project.service.adSlot;



import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateRangeUtil {
    public static Pair<LocalDateTime, LocalDateTime> getRangeByType(String type, LocalDateTime latestTime) {
        if (latestTime == null) {
            throw new IllegalArgumentException("latestTime은 null일 수 없습니다.");
        }

        LocalDate latestDate = latestTime.toLocalDate();
        LocalDateTime start;
        LocalDateTime end;

        switch (type) {
            case "day":
                // 하루 전 날짜의 00:00 ~ 23:59
                LocalDate oneDayBefore = latestDate.minusDays(1);
                start = oneDayBefore.atStartOfDay(); // 00:00
                end = oneDayBefore.atTime(23, 59, 59); // 23:59:59
                break;

            case "week":
                start = latestDate.minusDays(6).atStartOfDay(); // 7일 전
                end = latestDate.atTime(23, 59, 59);
                break;

            case "month":
                start = latestDate.minusDays(29).atStartOfDay(); // 30일 전
                end = latestDate.atTime(23, 59, 59);
                break;

            default:
                throw new IllegalArgumentException("잘못된 type: " + type);
        }

        return Pair.of(start, end);
    }
}

