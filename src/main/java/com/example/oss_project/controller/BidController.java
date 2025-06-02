package com.example.oss_project.controller;

import com.example.oss_project.core.common.CommonResponseDto;

import com.example.oss_project.domain.request.bidHistory.BidMoneyRequestDto;
import com.example.oss_project.service.bid.BidAdSlotService;
import com.example.oss_project.service.bid.BidMoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ads/bid")
public class BidController {
    private final BidAdSlotService bidAdSlotService;
    private final BidMoneyService bidMoneyService;

    @GetMapping("/{adSlotId}/{adId}")
    public CommonResponseDto<?> bidAdSlot(
        @PathVariable Long adSlotId
    ){
        return CommonResponseDto.ok(bidAdSlotService.bidAdSlot(adSlotId));
    }

    @PostMapping("/{adSlotId}/{adId}")
    public CommonResponseDto<?> makeBidHistory(
        @RequestBody List<BidMoneyRequestDto> bidMoneyRequestDtoList,
        @PathVariable Long adSlotId,
        @PathVariable Long adId
    ){
        bidMoneyService.saveBidPrice(bidMoneyRequestDtoList,adSlotId,adId);
        return CommonResponseDto.ok(true);
    }
}
