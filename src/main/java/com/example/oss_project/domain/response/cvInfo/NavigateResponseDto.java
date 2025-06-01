package com.example.oss_project.domain.response.cvInfo;

import java.time.LocalDateTime;

public record NavigateResponseDto(
        String adName,
        Long avgBidMoney,
        LocalDateTime startAdTime,
        LocalDateTime endAdTime
) {
}
