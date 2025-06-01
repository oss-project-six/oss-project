package com.example.oss_project.domain.response.cvInfo;

import java.util.List;

public record ViewCountResponseDto(
        List<Long> timeViewCount, // 시간대별 통행량
        Long avgViewCount
) {
}
