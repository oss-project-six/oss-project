package com.example.oss_project.service.ad;

import com.example.oss_project.domain.response.ad.AdSummaryResponseDto;
import com.example.oss_project.domain.response.adSlot.AdSlotSummaryResponseDto;
import com.example.oss_project.domain.entity.*;
import com.example.oss_project.domain.response.adSlot.CvInfoDto;
import com.example.oss_project.repository.ad.AdRepository;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryJpaRepository;
import com.example.oss_project.repository.cvInfo.CvInfoJpaRepository;
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
    private final BidHistoryJpaRepository bidHistoryRepository;
    private final CvInfoJpaRepository cvInfoRepository;

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
                    bidHistory != null ? bidHistory.getBidMoney() : null,
                    cvInfo != null ? cvInfo.getExposureScore() : null,
                    cvInfo != null ? cvInfo.getViewCount() : null
            );
        }).collect(Collectors.toList());
    }


    // 광고 하나에 대한 광고자리 + 입찰/노출 정보 반환
    public List<AdSlotSummaryResponseDto> getAdSlotsWithBidAndCvInfo(Long adId) {
        List<BidHistory> bidHistories = bidHistoryRepository.findByAd_AdId(adId);

        // 전체 midTime 값을 모으기 위한 리스트
        List<Double> allMidTimes = new ArrayList<>();

        // 각 광고 자리 요약 생성
        List<AdSlotSummaryResponseDto> result = bidHistories.stream()
                .map(bidHistory -> {
                    AdSlot adSlot = bidHistory.getAdSlot();

                    List<CvInfo> cvInfos = adSlot != null ? cvInfoRepository.findByAdSlot(adSlot) : List.of();
                    List<CvInfoDto> cvInfoDtoList = cvInfos.stream().map(cv -> {
                        if (cv.getMidTime() != null) allMidTimes.add(cv.getMidTime());
                        return new CvInfoDto(
                                cv.getMidTime(), // 필드명 주의!
                                cv.getExposureScore(),
                                cv.getAttentionRatio(),
                                cv.getViewCount()
                        );
                    }).toList();

                    return new AdSlotSummaryResponseDto(
                            adSlot != null ? adSlot.getLocalName() : null,
                            bidHistory.getBidMoney(),
                            bidHistory.getBidStatus() != null ? bidHistory.getBidStatus().ordinal() : null,
                            cvInfoDtoList,
                            null // 평균값은 나중에 set (임시)
                    );
                })
                .collect(Collectors.toList());

        // 평균 midTime 계산
        Double overallMidTimeAvg = allMidTimes.isEmpty() ? null :
                allMidTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        // 각 Dto에 평균값 세팅(필요하다면 새 리스트로 map)
        List<AdSlotSummaryResponseDto> resultWithAvg = result.stream()
                .map(dto -> new AdSlotSummaryResponseDto(
                        dto.localName(),
                        dto.bidMoney(),
                        dto.bidStatus(),
                        dto.cvInfoList(),
                        overallMidTimeAvg // 평균값 넣기
                ))
                .collect(Collectors.toList());

        return resultWithAvg;
    }



}
