package com.example.oss_project.service.bid;

import com.example.oss_project.domain.entity.Ad;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.request.bidHistory.BidMoneyRequestDto;
import com.example.oss_project.domain.type.BidStatus;
import com.example.oss_project.repository.ad.AdRepository;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryRepository;
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

    public void saveBidPrice(List<BidMoneyRequestDto> bidPrices, Long adSlotId, Long adId) {

        Ad ad = adRepository.findByAdId(adId)
                .orElseThrow(() -> new IllegalArgumentException("광고가 존재하지 않습니다."));

        AdSlot adSlot = adSlotRepository.findByAdSlotId(adSlotId);


        LocalDate today = LocalDate.now();

        List<BidHistory> bidHistories = bidPrices.stream()
                .map(dto -> {
                    LocalDate endDate = dto.startTime().isAfter(dto.endTime()) ? today.plusDays(1) : today;

                    return BidHistory.bidBuilder()
                            .ad(ad)
                            .adSlot(adSlot)
                            .bidMoney(dto.bidMoney())
                            .bidStatus(BidStatus.BIDDING)
                            .bidStartTime(LocalDateTime.of(today, dto.startTime()))
                            .bidEndTime(LocalDateTime.of(endDate, dto.endTime()))
                            .build();
                })
                .toList();

        bidHistoryRepository.saveAll(bidHistories);
    }

}