package com.example.oss_project.controller;

import com.example.oss_project.domain.dto.AdRegisterRequestDto;
import com.example.oss_project.domain.dto.AdSummaryResponseDto;
import com.example.oss_project.service.other.AdService;
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
    public ResponseEntity<List<AdSummaryResponseDto>> getMyAds(@PathVariable Long userId) {
        List<AdSummaryResponseDto> ads = adService.getAdsByUserId(userId);
        return ResponseEntity.ok(ads);
    }

}