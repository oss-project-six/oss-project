package com.example.oss_project.domain.response.cvInfo;

import java.util.List;

public record ExposureScoreResponseDto(
        List<List<Double>> exposureScores, // 노출 점수 표
        Double avgExposureScore

) {
}
