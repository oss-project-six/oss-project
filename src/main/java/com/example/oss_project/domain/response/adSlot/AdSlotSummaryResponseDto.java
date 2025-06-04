package com.example.oss_project.domain.response.adSlot;

import java.time.LocalDateTime;
import java.util.List;

public record AdSlotSummaryResponseDto(
        Long asSlotId,
        String adSlotName,
        Long bidMoney,
        Integer bidStatus,
        LocalDateTime bidStartTime,
        LocalDateTime bidEndTime,
        List<CvInfoDto> cvInfoList
) {}