package com.example.oss_project.domain.response.adSlot;

public record AdminAdSlotResponseDto(
        String imageUrl,
        String adSlotName,
        String address,
        Integer bidStatus,
        Long bid,
        Long viewCount,
        Double competition // 추가: 경쟁률(소수점 둘째자리)
) {}

