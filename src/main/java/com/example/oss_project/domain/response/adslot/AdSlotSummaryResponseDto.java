package com.example.oss_project.domain.response.adslot;

import java.util.List;

public record AdSlotSummaryResponseDto(
        String localName,
        Long bid,
        Integer bidStatus,
        List<CvInfoDto> cvInfoList
) {}
