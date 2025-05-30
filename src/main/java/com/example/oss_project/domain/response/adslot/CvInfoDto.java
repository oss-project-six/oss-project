package com.example.oss_project.domain.response.adslot;

public record CvInfoDto(
        Double avgTime,
        Double exposureScore,
        Double attentionRatio,
        Long viewCount
) {}
