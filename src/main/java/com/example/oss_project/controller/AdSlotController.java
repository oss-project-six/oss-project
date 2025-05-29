package com.example.oss_project.controller;

import com.example.oss_project.domain.request.adslot.AdSlotRegisterRequestDto;
import com.example.oss_project.domain.request.adslot.AdSlotSearchRequestDto;
import com.example.oss_project.domain.response.adslot.AdSlotResponseDto;
import com.example.oss_project.service.adslot.AdSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ads/adslot")
public class AdSlotController {
    private final AdSlotService adSlotService;

    @PostMapping("/regist")
    public ResponseEntity<String> registerAdSlot(@RequestBody AdSlotRegisterRequestDto dto) {
        adSlotService.registerAdSlot(dto);
        return ResponseEntity.ok("광고 자리 등록 성공");
    }

    @PostMapping
    public ResponseEntity<List<AdSlotResponseDto>> searchAdSlots(
            @RequestBody AdSlotSearchRequestDto request) {
        List<AdSlotResponseDto> result = adSlotService.searchAdSlots(
                request.getRegions(),
                request.getBidStatus(),
                request.getPrice()
        );
        return ResponseEntity.ok(result);
    }
}
