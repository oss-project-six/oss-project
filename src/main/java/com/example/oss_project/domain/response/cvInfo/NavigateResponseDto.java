package com.example.oss_project.domain.response.cvInfo;

import com.example.oss_project.domain.type.AdSlotStatus;
import com.example.oss_project.domain.type.BidStatus;

import java.time.LocalDateTime;

public record NavigateResponseDto(
        String adName,
        Long avgBidMoney,
        LocalDateTime startAdTime,
        LocalDateTime endAdTime,
        AdSlotStatus bidStatus
) {
}
