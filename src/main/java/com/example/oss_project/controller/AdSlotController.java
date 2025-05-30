package com.example.oss_project.controller;

import com.example.oss_project.core.common.CommonResponseDto;
import com.example.oss_project.domain.request.adslot.AdSlotRegisterRequestDto;
import com.example.oss_project.domain.request.adslot.AdSlotSearchRequestDto;
import com.example.oss_project.domain.response.adslot.AdminAdSlotResponseDto;
import com.example.oss_project.domain.response.adslot.AdSlotResponseDto;
import com.example.oss_project.service.adslot.AdSlotService;
import com.example.oss_project.service.adslot.AdminAdSlotSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;
import com.example.oss_project.core.s3.S3Util;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ads/adslot")
public class AdSlotController {
    private final AdSlotService adSlotService;
    private final AdminAdSlotSearchService adminAdSlotSearchSevice;
    private final S3Util s3Util;

    @PostMapping("/regist")
    public CommonResponseDto<String> registerAdSlot(
            @RequestPart("dto") String dtoJson,              // JSON 문자열로 받음
            @RequestPart("image") MultipartFile imageFile
    ) {
        try {
            // JSON 문자열을 DTO(record)로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            AdSlotRegisterRequestDto dto = objectMapper.readValue(dtoJson, AdSlotRegisterRequestDto.class);

            String imageUrl = s3Util.upload(imageFile);
            adSlotService.registerAdSlot(dto, imageUrl);
            return CommonResponseDto.ok("광고 자리 등록 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponseDto.ok("에러: " + e.getMessage());
        }
    }


    @PostMapping
    public CommonResponseDto<List<AdSlotResponseDto>> searchAdSlots(
            @RequestBody AdSlotSearchRequestDto request) {
        List<AdSlotResponseDto> result = adSlotService.searchAdSlots(
                request.regions(),
                request.bidStatus(),
                request.price()
        );
        return CommonResponseDto.ok(result);
    }

    @GetMapping("/admin/{adminId}")
    public CommonResponseDto<List<AdminAdSlotResponseDto>> getAdSlotsByAdmin(
            @PathVariable Long adminId) {
        List<AdminAdSlotResponseDto> result = adminAdSlotSearchSevice.getAdSlotsByAdmin(adminId);
        return CommonResponseDto.ok(result);
    }
}
