package com.example.oss_project.domain.response.adslot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdSummaryResponseDto {
    private String localName;   // 광고 자리 이름
    private Long bid;           // BidHistory의 가격
    private Integer bidStatus;  // BidHistory의 상태 (enum은 int 변환)
    private Double avgTime;         // CvInfo
    private Double exposureScore;   // CvInfo
    private Double attentionRatio;  // CvInfo
    private Long viewCount;         // CvInfo
}