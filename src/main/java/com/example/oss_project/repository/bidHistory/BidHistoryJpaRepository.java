package com.example.oss_project.repository.bidHistory;

import com.example.oss_project.domain.entity.Ad;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.entity.CvInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BidHistoryJpaRepository extends JpaRepository<BidHistory, Long> {
    List<BidHistory> findByAd_AdId(Long adId);
    BidHistory findTopByAdOrderByBidIdDesc(Ad ad);
    List<BidHistory> findByAdSlot(AdSlot adSlot);
    Optional<BidHistory> findTopByAdSlotOrderByBidIdDesc(AdSlot adSlot); // 가장 최신 입찰가
    int countByAdSlot(AdSlot adSlot);
    List<BidHistory> findByAd(Ad ad);
    @Query("SELECT bh FROM BidHistory bh JOIN FETCH bh.ad WHERE bh.adSlot = :adSlot")
    List<BidHistory> findByAdSlotWithAd(@Param("adSlot") AdSlot adSlot);

    List<BidHistory> findByAdSlotAndBidStartTimeBetweenOrderByBidStartTimeDesc(
            AdSlot adSlot,
            LocalDateTime start,
            LocalDateTime end
    );
}
