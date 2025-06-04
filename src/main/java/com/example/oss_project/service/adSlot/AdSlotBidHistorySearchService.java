package com.example.oss_project.service.adSlot;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.entity.CvInfo;
import com.example.oss_project.domain.response.adSlot.AdSlotBidHistoryResponseDto;
import com.example.oss_project.domain.response.bidHistory.BidHistoryDetailDto;
import com.example.oss_project.domain.type.BidStatus;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.adSlot.AdslotJpaRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryJpaRepository;
import com.example.oss_project.repository.cvInfo.CvInfoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdSlotBidHistorySearchService {
    private final AdslotJpaRepository adSlotRepository;
    private final BidHistoryJpaRepository bidHistoryRepository;
    private final CvInfoJpaRepository cvInfoRepository;

    public AdSlotBidHistoryResponseDto getAdSlotBidHistory(Long adSlotId) {
        // 1. 광고자리 조회
        AdSlot adSlot = adSlotRepository.findById(adSlotId)
                .orElseThrow(() -> new RuntimeException("광고자리 없음"));

        // 2. 입찰 이력 전체 조회 (단방향)
        List<BidHistory> histories = bidHistoryRepository.findByAdSlotWithAd(adSlot);

        // 3. 노출 점수
        List<CvInfo> cvInfos = cvInfoRepository.findByAdSlot(adSlot);
        Double avgExposureScore = cvInfos.isEmpty() ? null :
                cvInfos.stream().mapToDouble(CvInfo::getExposureScore).average().orElse(0.0);

        // 4. 입찰 DTO 변환
        List<BidHistoryDetailDto> detailDtos = histories.stream().map(bh ->
                new BidHistoryDetailDto(
                        bh.getAd() != null ? bh.getAd().getName() : null,
                        bh.getBidStatus() != null ? bh.getBidStatus().ordinal() : null,
                        bh.getBidMoney(),
                        bh.getBidStartTime(),
                        bh.getBidEndTime()
                )
        ).toList();

        // 5. 총 매출 = "입찰 종료" 상태인 것의 낙찰가 합
        long totalRevenue = histories.stream()
                .filter(bh -> bh.getBidStatus() == BidStatus.SUCCESS) // 기존에 BifStatus.FAIL 뺌
                .mapToLong(BidHistory::getBidMoney)
                .sum();

        // 6. 평균 입찰 수 = 하루에 2시간 간격 입찰 -> 총 12개 이므로 광고 자리에 전체 bid 수 / 12
        double avgBidCount = histories.size() / 12.0;

        // 7. 총 게재시간 = '입찰 종료'인 것의 개수 * 2
        int totalExposureHour = (int) (histories.stream()
                .filter(bh -> bh.getBidStatus() == BidStatus.SUCCESS) // 기존에 BidStatus.FAIL 뺌
                .count() * 2);

        return new AdSlotBidHistoryResponseDto(
                detailDtos,
                totalRevenue,
                avgBidCount,
                totalExposureHour,
                avgExposureScore
        );
    }
}
