package com.example.oss_project.domain.response.ad;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdSummaryResponseDto {
    private String adName;
    private String adImageUrl;
    private Integer bidStatus;      // BidHistory
    private Long bid;               // BidHistory
    private Double exposureScore;   // CvInfo
    private Long viewCount;         // CvInfo
    // 추가 특성 가능
}

