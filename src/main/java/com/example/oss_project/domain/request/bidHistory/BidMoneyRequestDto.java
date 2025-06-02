package com.example.oss_project.domain.request.bidHistory;

import java.time.LocalTime;

public record BidMoneyRequestDto(
        LocalTime startTime,
        LocalTime endTime,
        Long bidMoney
) {
}
