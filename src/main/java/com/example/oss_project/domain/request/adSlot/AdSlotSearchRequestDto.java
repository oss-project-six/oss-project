package com.example.oss_project.domain.request.adSlot;

import java.util.List;

public record AdSlotSearchRequestDto(
        List<String> regions,   // ["서울", "경기"]
        String bidStatus,       // 예: "입찰"
        Long price              // 가격 (최고 입찰가 이하)
) {}
