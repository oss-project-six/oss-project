package com.example.oss_project.domain.response.adSlot;

import java.util.List;

public record AdSlotSummaryResponseDto(
        String localName,
        Long bidMoney,
        Integer bidStatus,
        List<CvInfoDto> cvInfoList
) {}