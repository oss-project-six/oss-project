package com.example.oss_project.service.adslot;

import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.request.adslot.AdSlotRegisterRequestDto;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.Admin;
import com.example.oss_project.domain.response.adslot.AdSlotResponseDto;
import com.example.oss_project.domain.type.BidStatus;
import com.example.oss_project.repository.adslot.AdSlotRepository;
import com.example.oss_project.repository.admin.AdminRepository;
import com.example.oss_project.repository.bidhistory.BidHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdSlotService {
    private final AdSlotRepository adSlotRepository;
    private final AdminRepository adminRepository;
    private final BidHistoryRepository bidHistoryRepository;

    @Transactional
    public void registerAdSlot(AdSlotRegisterRequestDto dto) {

        Admin admin = adminRepository.findById(dto.getAdminId())
                .orElseThrow(() -> new RuntimeException("관리자 없음"));

        AdSlot adSlot = AdSlot.builder()
                .localName(dto.getLocalName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .address(dto.getAddress())
                .size(dto.getSize())
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
                .collect(Collectors.toList());

        // ⭐ 여기가 핵심
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
                    .collect(Collectors.toList());

            Optional<BidHistory> maxBidHistoryOpt = filteredHistories.stream()
                    .max(Comparator.comparing(BidHistory::getBid));

            if (maxBidHistoryOpt.isPresent()) {
                BidHistory maxBidHistory = maxBidHistoryOpt.get();
                if (price == null || maxBidHistory.getBid() <= price) {
                    result.add(new AdSlotResponseDto(
                            adSlot.getAdSlotId(),
                            adSlot.getLocalName(),
                            adSlot.getAddress(),
                            maxBidHistory.getBid(),
                            maxBidHistory.getBidStatus() != null ? maxBidHistory.getBidStatus().ordinal() : null
                    ));
                }
            }
        }
        return result;
    }

}
