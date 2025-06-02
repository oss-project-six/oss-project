package com.example.oss_project.repository.bidHistory;

import com.example.oss_project.core.exception.CustomException;
import com.example.oss_project.core.exception.ErrorCode;
import com.example.oss_project.domain.entity.Ad;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BidHistoryRepository {

    private final BidHistoryJpaRepository bidHistoryJpaRepository;

    public List<BidHistory> saveAll(List<BidHistory> bidHistories){
        return bidHistoryJpaRepository.saveAll(bidHistories);
    }

    public List<BidHistory> findByAdSlot(AdSlot adSlot){
        return bidHistoryJpaRepository.findByAdSlot(adSlot);
    }

    public List<BidHistory> findByAd_AdId(Long adId){
        return bidHistoryJpaRepository.findByAd_AdId(adId);
    }
    public BidHistory findTopByAdSlotOrderByBidIdDesc(AdSlot adslot){
        return bidHistoryJpaRepository.findTopByAdSlotOrderByBidIdDesc(adslot)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BIDHISTORY));
    }

    public int countByAdSlot(AdSlot adSlot){
        return bidHistoryJpaRepository.countByAdSlot(adSlot);
    }

    public List<BidHistory> findByAdSlotAndTimeStampBetweenOrderByTimeStampDesc(
            AdSlot adSlot, LocalDateTime start, LocalDateTime end
    ){
        return bidHistoryJpaRepository.findByAdSlotAndBidStartTimeBetweenOrderByBidStartTimeDesc(adSlot,start,end);
    }

}
