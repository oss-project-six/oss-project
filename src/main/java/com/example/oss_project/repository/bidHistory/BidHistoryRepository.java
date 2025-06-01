package com.example.oss_project.repository.bidHistory;

import com.example.oss_project.domain.entity.Ad;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BidHistoryRepository {

    private final BidHistoryJpaRepository bidHistoryJpaRepository;

    public List<BidHistory> findByAdSlot(AdSlot adSlot){
        return bidHistoryJpaRepository.findByAdSlot(adSlot);
    }

    public List<BidHistory> findByAd_AdId(Long adId){
        return bidHistoryJpaRepository.findByAd_AdId(adId);
    }
    public BidHistory findTopByAdOrderByBidIdDesc(Ad ad){
        return bidHistoryJpaRepository.findTopByAdOrderByBidIdDesc(ad);
    }

}
