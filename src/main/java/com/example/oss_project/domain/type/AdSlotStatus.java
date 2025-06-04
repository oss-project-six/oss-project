package com.example.oss_project.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AdSlotStatus {
    BEFORE_BIDDING("입찰 전"),
    BID_CONTINUE(" 입찰 진행중"),
    AD_CONTINUE("광고 게재중");

    private final String adSlotStatus;
}