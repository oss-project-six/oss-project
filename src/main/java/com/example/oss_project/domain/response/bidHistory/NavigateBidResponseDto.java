package com.example.oss_project.domain.response.bidHistory;

import java.time.LocalDateTime;

public record NavigateBidResponseDto(
        String adSlotName,
        String address,
        String description,
        Long width,
        Long height,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
