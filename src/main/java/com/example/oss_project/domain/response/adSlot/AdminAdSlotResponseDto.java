package com.example.oss_project.domain.response.adSlot;

public record AdminAdSlotResponseDto(
        String imageUrl,
        String localName,
        String address,
        Integer bidStatus, // enum을 int로 반환
        Long bid,
        Long viewCount,    // CvInfo에서 가져온 노출수
        Integer bidCount   // 해당 광고자리에 대한 전체 입찰 수
) {}
