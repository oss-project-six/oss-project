package com.example.oss_project.repository.bidhistory;

import com.example.oss_project.domain.entity.Ad;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.type.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidHistoryRepository extends JpaRepository<BidHistory, Long> {
    List<BidHistory> findByAd_AdId(Long adId);
    BidHistory findTopByAdOrderByBidIdDesc(Ad ad);
    List<BidHistory> findByAdSlot(AdSlot adSlot);
}
