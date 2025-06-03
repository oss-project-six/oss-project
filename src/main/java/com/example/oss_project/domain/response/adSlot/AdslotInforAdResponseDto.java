package com.example.oss_project.domain.response.adSlot;

import com.example.oss_project.domain.response.cvInfo.AttentionRateResponseDto;
import com.example.oss_project.domain.response.cvInfo.ExposureScoreResponseDto;
import com.example.oss_project.domain.response.cvInfo.NavigateResponseDto;
import com.example.oss_project.domain.response.cvInfo.StayTimeResponseDto;
import com.example.oss_project.domain.response.cvInfo.ViewCountResponseDto;

import java.time.LocalDateTime;

public record AdslotInforAdResponseDto(
        LocalDateTime latestTime,
        NavigateResponseDto navigateResponseDto,
        ExposureScoreResponseDto exposureScoreResponseDto,
        AttentionRateResponseDto attentionRateResponseDto,
        ViewCountResponseDto viewCountResponseDto,
        StayTimeResponseDto stayTimeResponseDto
) {
}
