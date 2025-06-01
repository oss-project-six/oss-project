package com.example.oss_project.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AdSlotStatus {
    CONTINUE("진행중"),
    FINISH("입찰 완료");

    private final String adSlotStatus;
}
