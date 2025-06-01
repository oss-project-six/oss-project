package com.example.oss_project.domain.response.cvInfo;

import java.util.List;

public record StayTimeResponseDto(
        Double avgStaytime,
        List<CvInfoStayTimeDto> stayTimes
) {
}
