package com.example.oss_project.service.adSlot;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.entity.CvInfo;
import com.example.oss_project.domain.response.adSlot.AdminAdSlotListResponseDto;
import com.example.oss_project.domain.response.adSlot.AdminAdSlotResponseDto;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.adSlot.AdslotJpaRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryJpaRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryRepository;
import com.example.oss_project.repository.cvInfo.CvInfoJpaRepository;
import com.example.oss_project.repository.cvInfo.CvInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminAdSlotSearchService {

    private final AdSlotRepository adSlotRepository;
    private final BidHistoryRepository bidHistoryRepository;
    private final CvInfoRepository cvInfoRepository;

    public AdminAdSlotListResponseDto getAdSlotsByAdmin(Long adminId) {
        List<AdSlot> adSlots = adSlotRepository.findByAdmin_AdminIdWithCvInfos(adminId);

        List<AdminAdSlotResponseDto> dtoList = adSlots.stream().map(adSlot -> {
            BidHistory bidHistory = null;
            try {
                bidHistory = bidHistoryRepository.findTopByAdSlotOrderByBidIdDesc(adSlot);
            } catch (Exception e) {
                bidHistory = null;
            }
            Long viewCount = adSlot.getCvInfos() != null && !adSlot.getCvInfos().isEmpty()
                    ? adSlot.getCvInfos().stream().mapToLong(CvInfo::getViewCount).sum()
                    : 0L;
            int bidCount = bidHistoryRepository.countByAdSlot(adSlot);
            double competition = Math.round(((double)bidCount / 12) * 100.0) / 100.0; // 경쟁률

            return new AdminAdSlotResponseDto(
                    adSlot.getImageUrl(),
                    adSlot.getAdSlotName(),
                    adSlot.getAdSlotId(),
                    adSlot.getAddress(),
                    bidHistory != null && bidHistory.getBidStatus() != null ? bidHistory.getBidStatus().ordinal() : null,
                    bidHistory != null ? bidHistory.getBidMoney() : null,
                    viewCount,
                    competition
            );
        }).toList();

        int totalAdSlotCount = dtoList.size();
        int finishedBidCount = (int) dtoList.stream()
                .filter(dto -> dto.bidStatus() != null && dto.bidStatus() == 2)
                .count();
        long totalViewCount = dtoList.stream().mapToLong(dto -> dto.viewCount() != null ? dto.viewCount() : 0).sum();
        long totalBidAmount = dtoList.stream().mapToLong(dto -> dto.bid() != null ? dto.bid() : 0).sum();

        return new AdminAdSlotListResponseDto(dtoList, totalAdSlotCount, adminId, finishedBidCount, totalViewCount, totalBidAmount);
    }

}
