package com.example.oss_project.domain.response.cvInfo;

import java.util.List;

public record AttentionRateResponseDto(
        Double avgAttentionRate, // 평균 응시율
        List<Double> attentionRations //시간대별 응시율
) {
}
