package com.example.oss_project.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdSummaryResponseDto {
    private String adName;
    private String adImageUrl;
    // 추가하고 싶은 특성 추가하여 광고 목록 조회에서 해당 특성 추가 가능, 엔티티에 추가해야 되는 경우도 있고 가공 데이터면 추가 안 해도 됨
}
