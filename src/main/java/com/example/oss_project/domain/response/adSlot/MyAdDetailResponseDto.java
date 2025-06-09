package com.example.oss_project.domain.response.adSlot;

import java.util.List;

public record MyAdDetailResponseDto(
        List<AdSlotSummaryResponseDto> slotList,
        Long adId,
        Long totalViewCount,
        Double avgExposureScore,
        Long totalBidMoney,
        Double overallMidTimeAvg    // 추가: 모든 광고 자리의 midTime 평균
) {}
