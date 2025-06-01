package com.example.oss_project.domain.response.cvInfo;

public record CvInfoStayTimeDto(
        String time,
        Double midTime,
        Double minTime,
        Double q1Time,
        Double q3Time,
        Double maxTime
) {
}
