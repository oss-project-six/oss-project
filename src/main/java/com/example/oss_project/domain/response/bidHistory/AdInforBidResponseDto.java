package com.example.oss_project.domain.response.bidHistory;

import java.util.List;

public record AdInforBidResponseDto(
        List<Long> avgBidMoney,
        List<Long> maxBidMoney,
        List<Long> avgTimeBidMoney
) {
}
