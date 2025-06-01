package com.example.oss_project.domain.response.adSlot;

import java.util.List;

public record ViewCountResponseDto(
        List<Long> viewCounts, // 시간대별 통행량
        Long total             // 총 통행량
) {
}
