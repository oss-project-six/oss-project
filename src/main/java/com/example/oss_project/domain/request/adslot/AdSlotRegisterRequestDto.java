package com.example.oss_project.domain.request.adslot;

public record AdSlotRegisterRequestDto(
        String localName,    // 광고 자리 이름
        String description,  // 광고 자리 설명
        String imageUrl,     // 광고 이미지 url
        String address,      // 광고 자리 주소
        String size,         // 광고 자리 크기
        Long adminId         // 관리자의 PK (토큰 없이 직접 전달)
) {}
