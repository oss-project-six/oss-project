package com.example.oss_project.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BidStatus {
    BEFORE_BIDDING("입찰 전"),
    BIDDING("입찰 중"),
    CLOSED("입찰 종료");

    private final String bidStatus;
}
