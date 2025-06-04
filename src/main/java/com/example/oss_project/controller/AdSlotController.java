package com.example.oss_project.controller;

import com.example.oss_project.core.common.CommonResponseDto;
import com.example.oss_project.core.security.CustomUserDetails;
import com.example.oss_project.domain.request.adSlot.AdSlotRegisterRequestDto;
import com.example.oss_project.domain.request.adSlot.AdSlotSearchRequestDto;
import com.example.oss_project.domain.response.adSlot.AdSlotChangeStatusRequestDto;
import com.example.oss_project.domain.response.adSlot.AdSlotResponseDto;
import com.example.oss_project.service.adSlot.*;
import com.example.oss_project.domain.response.adSlot.AdSlotBidHistoryResponseDto;
import com.example.oss_project.domain.response.adSlot.AdminAdSlotListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final AdminAdSlotSearchService adminAdSlotSearchService;
    private final AdSlotBidHistorySearchService adSlotBidHistorySearchService;
    private final ChangeAdSlotStatus changeAdSlotStatus;
    private final InforAdSlotService inforAdSlotService;
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

    @GetMapping("/infor/{adSlotId}")
    public CommonResponseDto<?> infoAdSlot(
            @PathVariable Long adSlotId,
            @RequestParam(defaultValue = "week") String type  // "week" or "month"
    ){
        return CommonResponseDto.ok(inforAdSlotService.findBasicInfor(adSlotId,type));
    }

    @GetMapping("/admin/{adminId}")
    public CommonResponseDto<AdminAdSlotListResponseDto> getAdSlotsByAdmin(@PathVariable Long adminId) {
        return CommonResponseDto.ok(adminAdSlotSearchService.getAdSlotsByAdmin(adminId));
    }


    @GetMapping("/{adSlotId}")
    public CommonResponseDto<AdSlotBidHistoryResponseDto> getAdSlotBidHistory(
            @PathVariable Long adSlotId) {
        return CommonResponseDto.ok(adSlotBidHistorySearchService.getAdSlotBidHistory(adSlotId));
    }

    @PostMapping("/change")
    public CommonResponseDto<String> changeAdSlotStatus(
            @RequestBody AdSlotChangeStatusRequestDto request
            ) {
        changeAdSlotStatus.changeStatusToContinue(request.adSlotId());
        return CommonResponseDto.ok("성공.");
    }
}
