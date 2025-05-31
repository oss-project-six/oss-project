package com.example.oss_project.domain.response.ad;

import java.util.List;

public record MyAdSummaryListResponseDto(
        List<AdSummaryResponseDto> adList,
        int totalAdCount,
        int completedBidCount,
        long totalViewCount,
        long totalBidMoney
) {}
