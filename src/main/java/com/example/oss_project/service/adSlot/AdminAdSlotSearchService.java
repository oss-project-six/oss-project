package com.example.oss_project.service.adslot;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.entity.CvInfo;
import com.example.oss_project.domain.response.adslot.AdminAdSlotResponseDto;
import com.example.oss_project.repository.adSlot.AdslotJpaRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryJpaRepository;
import com.example.oss_project.repository.cvInfo.CvInfoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminAdSlotSearchService {

    private final AdslotJpaRepository adSlotRepository;
    private final BidHistoryJpaRepository bidHistoryRepository;
    private final CvInfoJpaRepository cvInfoRepository;

    public List<AdminAdSlotResponseDto> getAdSlotsByAdmin(Long adminId) {
        List<AdSlot> adSlots = adSlotRepository.findByAdmin_AdminId(adminId);

        return adSlots.stream().map(adSlot -> {
            // 최신 입찰 이력
            BidHistory bidHistory = bidHistoryRepository.findTopByAdSlotOrderByBidIdDesc(adSlot);

            // CvInfo viewCount (여러 개면 가장 최근, 또는 첫 번째로, 필요에 따라 수정)
            Long viewCount = 0L;
            List<CvInfo> cvInfoList = cvInfoRepository.findByAdSlot(adSlot);
            if (cvInfoList != null && !cvInfoList.isEmpty()) {
                // 최근 데이터 사용 (예: 첫 번째)
                viewCount = cvInfoList.get(0).getViewCount();
            }

            // 해당 광고자리에 대한 전체 입찰 수
            int bidCount = bidHistoryRepository.countByAdSlot(adSlot);

            return new AdminAdSlotResponseDto(
                    adSlot.getImageUrl(),
                    adSlot.getLocalName(),
                    adSlot.getAddress(),
                    bidHistory != null && bidHistory.getBidStatus() != null ? bidHistory.getBidStatus().ordinal() : null,
                    bidHistory != null ? bidHistory.getBidMoney() : null,
                    viewCount,
                    bidCount
            );
        }).toList();
    }

}

