package com.example.oss_project.domain.response.adSlot;

import com.example.oss_project.domain.response.bidHistory.BidHistoryDetailDto;

import java.util.List;

public record AdSlotBidHistoryResponseDto(
        List<BidHistoryDetailDto> histories, // 입찰 내역
        Long totalRevenue,                   // 총 매출
        Double avgBidCount,                  // 평균 입찰 수
        Integer totalExposureHour,           // 총 게재시간(시간 단위)
        Double exposureScore                 // 노출도
) {}