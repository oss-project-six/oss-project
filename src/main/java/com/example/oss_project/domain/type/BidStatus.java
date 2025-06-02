package com.example.oss_project.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BidStatus {
    BEFORE_BIDDING("입찰 전"),
    BIDDING("입찰 중"),
    SUCCESS("입찰 성공"),
    FAIL("입찰 실패");

    private final String bidStatus;
}
