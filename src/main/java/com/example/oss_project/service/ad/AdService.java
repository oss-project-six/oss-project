package com.example.oss_project.service.ad;

import com.example.oss_project.domain.response.ad.AdSummaryResponseDto;
import com.example.oss_project.domain.response.adslot.AdSlotSummaryResponseDto;
import com.example.oss_project.domain.entity.*;
import com.example.oss_project.domain.response.adslot.CvInfoDto;
import com.example.oss_project.repository.ad.AdRepository;
import com.example.oss_project.repository.adslot.AdSlotRepository;
import com.example.oss_project.repository.bidhistory.BidHistoryRepository;
import com.example.oss_project.repository.cvinfo.CvInfoRepository;
import com.example.oss_project.repository.user.UserRepository;
import com.example.oss_project.domain.request.ad.AdRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdService {
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdSlotRepository adSlotRepository;
    private final BidHistoryRepository bidHistoryRepository;
    private final CvInfoRepository cvInfoRepository;

    // AdService.java
    @Transactional
    public void registerAd(AdRegisterRequestDto dto, String imageUrl) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Ad ad = Ad.builder()
                .name(dto.name())
                .description(dto.description())
                .imageUrl(imageUrl) // ← S3에서 받은 URL을 여기서 사용!
                .category(dto.category())
                .user(user)
                .build();

        adRepository.save(ad);
    }


    // 광고주가 등록한 광고 리스트 반환
    public List<AdSummaryResponseDto> getAdsByUserId(Long userId) {
        List<Ad> ads = adRepository.findByUser_UserId(userId);

        return ads.stream().map(ad -> {
            BidHistory bidHistory = bidHistoryRepository.findTopByAdOrderByBidIdDesc(ad);

            List<CvInfo> cvInfos = null;
            if (bidHistory != null && bidHistory.getAdSlot() != null) {
                cvInfos = cvInfoRepository.findByAdSlot(bidHistory.getAdSlot());
            }

            CvInfo cvInfo = null;
            if (cvInfos != null && !cvInfos.isEmpty()) {
                cvInfo = cvInfos.stream()
                        .sorted((a, b) -> b.getTimeStamp().compareTo(a.getTimeStamp()))
                        .findFirst()
                        .orElse(null);
            }

            return new AdSummaryResponseDto(
                    ad.getName(),
                    ad.getImageUrl(),
                    bidHistory != null && bidHistory.getBidStatus() != null ? bidHistory.getBidStatus().ordinal() : null,
                    bidHistory != null ? bidHistory.getBid() : null,
                    cvInfo != null ? cvInfo.getExposureScore() : null,
                    cvInfo != null ? cvInfo.getViewCount() : null
            );
        }).collect(Collectors.toList());
    }


    // 광고 하나에 대한 광고자리 + 입찰/노출 정보 반환
    public List<AdSlotSummaryResponseDto> getAdSlotsWithBidAndCvInfo(Long adId) {
        List<BidHistory> bidHistories = bidHistoryRepository.findByAd_AdId(adId);

        return bidHistories.stream()
                .map(bidHistory -> {
                    AdSlot adSlot = bidHistory.getAdSlot();

                    // 여러 CvInfo 반환
                    List<CvInfo> cvInfos = adSlot != null ? cvInfoRepository.findByAdSlot(adSlot) : List.of();

                    // CvInfo → CvInfoDto 변환
                    List<CvInfoDto> cvInfoDtoList = cvInfos.stream()
                            .map(cvInfo -> new CvInfoDto(
                                    cvInfo.getAvgTime(),
                                    cvInfo.getExposureScore(),
                                    cvInfo.getAttentionRatio(),
                                    cvInfo.getViewCount()
                            ))
                            .collect(Collectors.toList());

                    return new AdSlotSummaryResponseDto(
                            adSlot != null ? adSlot.getLocalName() : null,
                            bidHistory.getBid(),
                            bidHistory.getBidStatus() != null ? bidHistory.getBidStatus().ordinal() : null,
                            cvInfoDtoList   // DTO 리스트로 반환!
                    );
                })
                .collect(Collectors.toList());
    }


}
