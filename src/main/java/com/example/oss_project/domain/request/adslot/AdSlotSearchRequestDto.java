package com.example.oss_project.domain.request.adslot;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AdSlotSearchRequestDto {
    private List<String> regions;   // ["서울", "경기"]
    private String bidStatus;       // 예: "입찰"
    private Long price;             // 가격 (최고 입찰가 이하)
}