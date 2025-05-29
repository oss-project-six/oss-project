package com.example.oss_project.service.ad;

import com.example.oss_project.domain.response.adslot.AdSummaryResponseDto;
import com.example.oss_project.domain.entity.*;
import com.example.oss_project.repository.ad.AdRepository;
import com.example.oss_project.repository.adslot.AdSlotRepository;
import com.example.oss_project.repository.bidhistory.BidHistoryRepository;
import com.example.oss_project.repository.cvinfo.CvInfoRepository;
import com.example.oss_project.repository.user.UserRepository;
import com.example.oss_project.domain.request.ad.AdRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void registerAd(AdRegisterRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Ad ad = Ad.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .category(dto.getCategory())
                .user(user)
                .build();

        adRepository.save(ad);
    }

    public List<com.example.oss_project.domain.response.ad.AdSummaryResponseDto> getAdsByUserId(Long userId) {
        List<Ad> ads = adRepository.findByUser_UserId(userId);

        return ads.stream().map(ad -> {
            // BidHistory - 광고의 가장 최근 입찰이력 1개 (가장 최근 bid_id 등으로)
            BidHistory bidHistory = bidHistoryRepository.findTopByAdOrderByBidIdDesc(ad);
            // CvInfo - 광고의 광고자리들에 대한 노출 정보 1개(예: 가장 최신 ad_slot에서)
            CvInfo cvInfo = null;
            if (bidHistory != null && bidHistory.getAdSlot() != null) {
                cvInfo = cvInfoRepository.findByAdSlot(bidHistory.getAdSlot());
            }

            return new com.example.oss_project.domain.response.ad.AdSummaryResponseDto(
                    ad.getName(),
                    ad.getImageUrl(),
                    bidHistory != null && bidHistory.getBidStatus() != null ? bidHistory.getBidStatus().ordinal() : null, // enum → int
                    bidHistory != null ? bidHistory.getBid() : null,
                    cvInfo != null ? cvInfo.getExposureScore() : null,
                    cvInfo != null ? cvInfo.getViewCount() : null
            );

        }).collect(Collectors.toList());
    }


    public List<AdSummaryResponseDto> getAdSlotsWithBidAndCvInfo(Long adId) {
        List<BidHistory> bidHistories = bidHistoryRepository.findByAd_AdId(adId);

        return bidHistories.stream()
                .map(bidHistory -> {
                    AdSlot adSlot = bidHistory.getAdSlot();
                    CvInfo cvInfo = adSlot != null ? adSlot.getCvInfo() : null;

                    return new AdSummaryResponseDto(
                            adSlot != null ? adSlot.getLocalName() : null,
                            bidHistory.getBid(),
                            bidHistory.getBidStatus() != null ? bidHistory.getBidStatus().ordinal() : null, // Enum → int
                            cvInfo != null ? cvInfo.getAvgTime() : null,
                            cvInfo != null ? cvInfo.getExposureScore() : null,
                            cvInfo != null ? cvInfo.getAttentionRatio() : null,
                            cvInfo != null ? cvInfo.getViewCount() : null
                    );
                })
                .collect(Collectors.toList());
    }

}