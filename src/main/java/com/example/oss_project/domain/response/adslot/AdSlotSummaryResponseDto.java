package com.example.oss_project.domain.response.adslot;

import java.util.List;

public record AdSlotSummaryResponseDto(
        String localName,
        Long bidMoney,
        Integer bidStatus,
        List<CvInfoDto> cvInfoList,
        Double overallMidTimeAvg    // 추가: 모든 광고 자리의 midTime 평균
) {}