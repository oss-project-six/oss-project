package com.example.oss_project.domain.request.ad;

public record AdRegisterRequestDto(
        Long userId,
        String name,
        String description,
        String imageUrl,
        String category
) {}
