package com.example.oss_project.service.bid;

import com.example.oss_project.domain.entity.Ad;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.request.bidHistory.BidMoneyRequestDto;
import com.example.oss_project.domain.type.BidStatus;
import com.example.oss_project.repository.ad.AdRepository;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidMoneyService {

    private final BidHistoryRepository bidHistoryRepository;
    private final AdRepository adRepository;
    private final AdSlotRepository adSlotRepository;

    @Transactional
    public void saveBidPrice(List<BidMoneyRequestDto> bidPrices,
                             Long adSlotId,
                             Long adId) {

        // 1) 광고, 슬롯 조회
        Ad ad = adRepository.findByAdId(adId)
                .orElseThrow(() -> new IllegalArgumentException("광고가 존재하지 않습니다."));
        AdSlot adSlot = adSlotRepository.findByAdSlotId(adSlotId);

        // 2) 기준이 될 '내일' 날짜 계산
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // 3) DTO → BidHistory 변환
        List<BidHistory> bidHistories = bidPrices.stream()
                .map(dto -> {
                    // 자정 넘어가는 입찰이면 내일→모레(endDate), 아니면 내일
                    LocalDate endDate = dto.startTime().isAfter(dto.endTime())
                            ? tomorrow.plusDays(1)
                            : tomorrow;

                    return BidHistory.bidBuilder()
                            .ad(ad)
                            .adSlot(adSlot)
                            .bidMoney(dto.bidMoney())
                            .bidStatus(BidStatus.BIDDING)
                            // 항상 내일 날짜에 시작/종료 시각을 결합
                            .bidStartTime(LocalDateTime.of(tomorrow, dto.startTime()))
                            .bidEndTime(  LocalDateTime.of(endDate, dto.endTime()))
                            .build();
                })
                .toList();

        // 4) 배치 저장
        bidHistoryRepository.saveAll(bidHistories);
    }
}