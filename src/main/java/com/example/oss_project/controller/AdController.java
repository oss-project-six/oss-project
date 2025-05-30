package com.example.oss_project.controller;

import com.example.oss_project.core.common.CommonResponseDto;
import com.example.oss_project.core.exception.CustomException;
import com.example.oss_project.core.exception.ErrorCode;
import com.example.oss_project.domain.request.ad.AdRegisterRequestDto;
import com.example.oss_project.domain.response.adSlot.AdSlotSummaryResponseDto;
import com.example.oss_project.service.ad.AdService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import com.example.oss_project.core.s3.S3Util;
import com.fasterxml.jackson.core.JsonProcessingException;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ads")
public class AdController {

    private final AdService adService;
    private final S3Util s3Util;

    @PostMapping("/regist")
    public CommonResponseDto<?> registerAd(
            @RequestPart("dto") String dto,
            @RequestPart("image") MultipartFile imageFile
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AdRegisterRequestDto reqDto = objectMapper.readValue(dto, AdRegisterRequestDto.class);

            String imageUrl = s3Util.upload(imageFile);
            adService.registerAd(reqDto, imageUrl);

            return CommonResponseDto.ok("광고 등록 성공");
        } catch (JsonProcessingException e) {
            return CommonResponseDto.fail(
                    new CustomException(ErrorCode.INVALID_REQUEST_BODY)
            );
        }
    }

    @GetMapping("/my/user/{userId}")
    public CommonResponseDto<List<com.example.oss_project.domain.response.ad.AdSummaryResponseDto>> getMyAds(@PathVariable Long userId) {
        return CommonResponseDto.ok(adService.getAdsByUserId(userId));
    }

    @GetMapping("/my/ad/{adId}")
    public CommonResponseDto<List<AdSlotSummaryResponseDto>> getAdSlotsByAd(@PathVariable Long adId) {
        List<AdSlotSummaryResponseDto> result = adService.getAdSlotsWithBidAndCvInfo(adId);
        return CommonResponseDto.ok(result);
    }
}
