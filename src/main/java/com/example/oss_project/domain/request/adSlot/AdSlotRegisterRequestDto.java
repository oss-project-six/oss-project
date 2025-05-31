package com.example.oss_project.domain.request.adSlot;

import com.example.oss_project.domain.request.minPrice.MinPriceRegisterRequestDto;

import java.util.List;

public record AdSlotRegisterRequestDto(
        String slotName,
        String description,
        String imageUrl,
        String address,
        String size,
        Long adminId,
        List<MinPriceRegisterRequestDto> minPriceList // << 추가!
) {}
