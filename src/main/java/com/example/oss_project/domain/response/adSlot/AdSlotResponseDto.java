package com.example.oss_project.domain.response.adSlot;

public record AdSlotResponseDto(
        Long adSlotId,
        String SlotName,
        String address,
        Long bid,          // 최고 입찰가
        Integer bidStatus  // 입찰 상태
        // 필요시 추가 특성
) {}
