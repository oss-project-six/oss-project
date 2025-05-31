package com.example.oss_project.domain.response.bidHistory;

import java.time.LocalDateTime;

public record BidHistoryDetailDto(
        String adName,      // 광고명
        Integer bidStatus,  // BidStatus (enum->int)
        Long bid,           // 입찰가
        LocalDateTime bidStartTime,
        LocalDateTime bidEndTime
) {}