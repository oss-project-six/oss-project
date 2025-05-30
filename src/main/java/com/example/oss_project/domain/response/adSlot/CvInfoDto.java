package com.example.oss_project.domain.response.adSlot;

public record CvInfoDto(
        Double midTime,         // avgTime → midTime
        Double exposureScore,
        Double attentionRatio,
        Long viewCount
) {}
