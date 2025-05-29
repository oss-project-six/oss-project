package com.example.oss_project.domain.response.adslot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdSlotResponseDto {
    private Long adSlotId;
    private String localName;
    private String address;
    private Long bid;          // 최고 입찰가
    private Integer bidStatus; // 입찰 상태 (enum이면 int)
    // 필요시 추가 특성
}