package com.example.oss_project.domain.response.adSlot;

import java.util.List;

public record AdminAdSlotListResponseDto(
        List<AdminAdSlotResponseDto> adSlots,
        int totalAdSlotCount,
        Long adminId,
        int finishedBidCount,
        long totalViewCount,
        long totalBidAmount
) {}
