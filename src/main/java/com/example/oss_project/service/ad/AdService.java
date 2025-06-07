package com.example.oss_project.service.ad;

import com.example.oss_project.domain.response.ad.AdSummaryResponseDto;
import com.example.oss_project.domain.response.ad.MyAdSummaryListResponseDto;
import com.example.oss_project.domain.response.adSlot.AdSlotSummaryResponseDto;
import com.example.oss_project.domain.entity.*;
import com.example.oss_project.domain.response.adSlot.CvInfoDto;
import com.example.oss_project.domain.response.adSlot.MyAdDetailResponseDto;
import com.example.oss_project.domain.type.BidStatus;
import com.example.oss_project.repository.ad.AdRepository;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryJpaRepository;
import com.example.oss_project.repository.cvInfo.CvInfoJpaRepository;
import com.example.oss_project.repository.user.UserRepository;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryRepository;
import com.example.oss_project.repository.cvInfo.CvInfoRepository;
import com.example.oss_project.repository.user.UserJpaRepository;
import com.example.oss_project.domain.request.ad.AdRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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
    public MyAdSummaryListResponseDto getAdsByUserIdWithStats(Long userId) {
        List<Ad> ads = adRepository.findByUser_UserId(userId);

        AtomicInteger completedBidAdCount = new AtomicInteger(0);
        AtomicLong totalViewCount = new AtomicLong(0);
        AtomicLong totalBidMoney = new AtomicLong(0);

        List<AdSummaryResponseDto> adSummaries = ads.stream().map(ad -> {
            // 현재 시각에 해당하는 입찰 기록 조회
            LocalDateTime now = LocalDateTime.now();
            List<BidHistory> histories = bidHistoryRepository.findByAd(ad);
            BidHistory bidHistory = histories.stream()
                    .filter(bh -> !bh.getBidStartTime().isAfter(now) && bh.getBidEndTime().isAfter(now))
                    .findFirst()
                    .orElse(null);

            Integer bidStatusOrdinal = (bidHistory != null && bidHistory.getBidStatus() != null)
                    ? bidHistory.getBidStatus().ordinal() : BidStatus.BEFORE_BIDDING.ordinal();
            Long bidMoney = (bidHistory != null) ? bidHistory.getBidMoney() : 0;

            // cv_info를 입찰 시간대 기준으로 찾아야 할 경우
            Double exposureScore = 0.0;
            Long viewCount = 0L;

            if (bidHistory != null && bidHistory.getAdSlot() != null && bidHistory.getBidStartTime() != null) {
                List<CvInfo> cvInfos = cvInfoRepository.findByAdSlot(bidHistory.getAdSlot());
                CvInfo matchedCvInfo = cvInfos.stream()
                        .filter(cv -> cv.getTimeStamp().equals(bidHistory.getBidStartTime()))
                        .findFirst()
                        .orElse(null);
                if (matchedCvInfo != null) {
                    exposureScore = matchedCvInfo.getExposureScore();
                    viewCount = matchedCvInfo.getViewCount();
                    totalViewCount.addAndGet(viewCount);
                }
            }

            // 입찰 완료 광고 수 카운트 (예: BidStatus가 "입찰 완료" = 1)
            if (bidStatusOrdinal == 1) {
                completedBidAdCount.incrementAndGet();
                if (bidMoney != null) {
                    totalBidMoney.addAndGet(bidMoney);
                }
            }

            return new AdSummaryResponseDto(
                    ad.getName(),
                    ad.getImageUrl(),
                    bidStatusOrdinal,
                    bidMoney,
                    exposureScore,
                    viewCount
            );
        }).collect(Collectors.toList());

        return new MyAdSummaryListResponseDto(
                adSummaries,
                ads.size(),
                completedBidAdCount.get(),
                totalViewCount.get(),
                totalBidMoney.get()
        );
    }


    // 광고 하나에 대한 광고자리 + 입찰/노출 정보 반환
    @Transactional(readOnly = true)
    public MyAdDetailResponseDto getAdSlotsWithBidAndCvInfo(Long adId) {
        List<BidHistory> bidHistories = bidHistoryRepository.findByAd_AdId(adId);

        List<AdSlotSummaryResponseDto> slotList = bidHistories.stream()
                .map(bidHistory -> {
                    AdSlot adSlot = bidHistory.getAdSlot();

                    // "bid_end_time"과 "cv_info_time_stamp"가 완전히 같은 CvInfo만 반환
                    List<CvInfoDto> cvInfoDtos;
                    if (adSlot != null && bidHistory.getBidEndTime() != null) {
                        Optional<CvInfo> cvInfoOpt = cvInfoRepository.findByAdSlotAndTimeStamp(
                                adSlot, bidHistory.getBidEndTime());
                        cvInfoDtos = cvInfoOpt
                                .map(cvInfo -> List.of(new CvInfoDto(
                                        cvInfo.getMidTime(),
                                        cvInfo.getExposureScore(),
                                        cvInfo.getAttentionRatio(),
                                        cvInfo.getViewCount()
                                )))
                                .orElse(List.of());
                    } else {
                        cvInfoDtos = List.of();
                    }

                    return new AdSlotSummaryResponseDto(
                            adSlot != null ? adSlot.getAdSlotId() :null,
                            adSlot != null ? adSlot.getAdSlotName() : null,
                            bidHistory.getBidMoney(),
                            bidHistory.getBidStatus() != null ? bidHistory.getBidStatus().ordinal() : null,
                            bidHistory.getBidStartTime(),
                            bidHistory.getBidEndTime(),
                            cvInfoDtos
                    );
                })
                .toList();

        // 집계 필드 계산
        long totalViewCount = slotList.stream()
                .flatMap(slot -> slot.cvInfoList().stream())
                .mapToLong(CvInfoDto::viewCount)
                .sum();

        double avgExposureScore = slotList.stream()
                .flatMap(slot -> slot.cvInfoList().stream())
                .mapToDouble(CvInfoDto::exposureScore)
                .average()
                .orElse(0.0);

        long totalBidMoney = slotList.stream()
                .mapToLong(slot -> slot.bidMoney() == null ? 0 : slot.bidMoney())
                .sum();

        List<Double> allMidTimes = slotList.stream()
                .flatMap(slot -> slot.cvInfoList().stream())
                .map(CvInfoDto::midTime)
                .filter(Objects::nonNull)
                .toList();
        Double overallMidTimeAvg = allMidTimes.isEmpty() ? null :
                allMidTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        return new MyAdDetailResponseDto(
                slotList,
                totalViewCount,
                avgExposureScore,
                totalBidMoney,
                overallMidTimeAvg
        );
    }
}
