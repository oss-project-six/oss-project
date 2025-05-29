package com.example.oss_project.domain.request.adslot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdSlotRegisterRequestDto {
    private String localName;     // 광고 자리 이름
    private String description;   // 광고 자리 설명
    private String imageUrl;      // 광고 이미지 url
    private String address;       // 광고 자리 주소
    private String size;          // 광고 자리 크기
    private Long adminId;         // 관리자의 PK (토큰 없이 직접 전달)
}

