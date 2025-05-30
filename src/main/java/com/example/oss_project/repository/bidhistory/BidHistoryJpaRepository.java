package com.example.oss_project.repository.bidHistory;

import com.example.oss_project.domain.entity.Ad;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidHistoryJpaRepository extends JpaRepository<BidHistory,Long> {
    List<BidHistory> findByAdSlot(AdSlot adSlot);
    List<BidHistory> findByAd_AdId(Long adId);
    BidHistory findTopByAdOrderByBidIdDesc(Ad ad);

}
