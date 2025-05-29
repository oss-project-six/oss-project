package com.example.oss_project.controller;

import com.example.oss_project.domain.request.ad.AdRegisterRequestDto;
import com.example.oss_project.domain.response.adslot.AdSummaryResponseDto;
import com.example.oss_project.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ads")
public class AdController {

    private final AdService adService;

    @PostMapping("/regist")
    public ResponseEntity<?> registerAd(@RequestBody AdRegisterRequestDto dto) {
        adService.registerAd(dto);
        return ResponseEntity.ok("광고 등록 성공");
    }

    @GetMapping("/my/user/{userId}")
    public ResponseEntity<List<com.example.oss_project.domain.response.ad.AdSummaryResponseDto>> getMyAds(@PathVariable Long userId) {
        return ResponseEntity.ok(adService.getAdsByUserId(userId));
    }


    @GetMapping("/my/ad/{adId}")
    public ResponseEntity<List<AdSummaryResponseDto>> getAdSlotsByAd(@PathVariable Long adId) {
        List<AdSummaryResponseDto> result = adService.getAdSlotsWithBidAndCvInfo(adId);
        return ResponseEntity.ok(result);
    }

}