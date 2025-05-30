package com.example.oss_project.domain.request.minPrice;

public record MinPriceRegisterRequestDto(
        String startTime, // "00:00"
        String endTime,   // "02:00"
        Long price
) {}