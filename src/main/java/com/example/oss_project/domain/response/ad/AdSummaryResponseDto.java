package com.example.oss_project.domain.response.ad;

public record AdSummaryResponseDto(
        String adName,
        String adImageUrl,
        Integer bidStatus,      // BidHistory
        Long bid,               // BidHistory
        Double exposureScore,   // CvInfo
        Long viewCount,         // CvInfo
        Long adId
        // 추가 특성 가능
) {}
