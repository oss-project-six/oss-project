package com.example.oss_project.domain.response.adslot;

public record CvInfoDto(
        Double midTime,         // avgTime → midTime
        Double exposureScore,
        Double attentionRatio,
        Long viewCount
) {}
