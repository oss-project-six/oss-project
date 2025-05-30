package com.example.oss_project.service.adSlot;

import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.request.adslot.AdSlotRegisterRequestDto;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.Admin;
import com.example.oss_project.domain.response.adslot.AdSlotResponseDto;
import com.example.oss_project.domain.type.BidStatus;
import com.example.oss_project.repository.admin.AdminRepository;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdSlotService {
    private final AdSlotRepository adSlotRepository;
    private final AdminRepository adminRepository;
    private final BidHistoryRepository bidHistoryRepository;

    @Transactional
    public void registerAdSlot(AdSlotRegisterRequestDto dto, String imageUrl) {
        Admin admin = adminRepository.findById(dto.adminId())
                .orElseThrow(() -> new RuntimeException("관리자 없음"));

        AdSlot adSlot = AdSlot.builder()
                .localName(dto.localName())
                .description(dto.description())
                .imageUrl(imageUrl)
                .address(dto.address())
                .size(dto.size())
                .admin(admin)
                .build();

        adSlotRepository.save(adSlot);
    }


    public List<AdSlotResponseDto> searchAdSlots(List<String> regions, String bidStatusStr, Long price) {
        List<AdSlot> allSlots = adSlotRepository.findAll();

        List<AdSlot> filteredByRegion = allSlots.stream()
                .filter(adSlot -> regions == null || regions.isEmpty()
                        || regions.stream().anyMatch(region ->
                        adSlot.getAddress() != null && adSlot.getAddress().contains(region)
                ))
                .toList();

        final BidStatus bidStatusFinal;
        if (bidStatusStr != null && !bidStatusStr.isEmpty()) {
            switch (bidStatusStr) {
                case "입찰 전": bidStatusFinal = BidStatus.BEFORE_BIDDING; break;
                case "입찰 중": bidStatusFinal = BidStatus.BIDDING; break;
                case "입찰 종료": bidStatusFinal = BidStatus.CLOSED; break;
                default: bidStatusFinal = null;
            }
        } else {
            bidStatusFinal = null;
        }

        List<AdSlotResponseDto> result = new ArrayList<>();
        for (AdSlot adSlot : filteredByRegion) {
            List<BidHistory> histories = bidHistoryRepository.findByAdSlot(adSlot);

            List<BidHistory> filteredHistories = histories.stream()
                    .filter(b -> bidStatusFinal == null || b.getBidStatus() == bidStatusFinal)
                    .toList();

            Optional<BidHistory> maxBidHistoryOpt = filteredHistories.stream()
                    .max(Comparator.comparing(BidHistory::getBidMoney));

            if (maxBidHistoryOpt.isPresent()) {
                BidHistory maxBidHistory = maxBidHistoryOpt.get();
                if (price == null || maxBidHistory.getBidMoney() <= price) {
                    result.add(new AdSlotResponseDto(
                            adSlot.getAdSlotId(),
                            adSlot.getLocalName(),
                            adSlot.getAddress(),
                            maxBidHistory.getBidMoney(),
                            maxBidHistory.getBidStatus() != null ? maxBidHistory.getBidStatus().ordinal() : null
                    ));
                }
            }
        }
        return result;
    }
}
