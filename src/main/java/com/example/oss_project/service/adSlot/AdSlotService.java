package com.example.oss_project.service.adSlot;

import com.example.oss_project.core.kakao.KakaoApiUtil;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.Admin;
import com.example.oss_project.domain.entity.MinPrice;
import com.example.oss_project.domain.request.adSlot.AdSlotRegisterRequestDto;
import com.example.oss_project.domain.request.minPrice.MinPriceRegisterRequestDto;
import com.example.oss_project.domain.response.adSlot.AdSlotResponseDto;
import com.example.oss_project.domain.type.BidStatus;
import com.example.oss_project.repository.admin.AdminRepository;
import com.example.oss_project.repository.minPrice.MinPriceRepository;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdSlotService {
    private final AdSlotRepository adSlotRepository;
    private final AdminRepository adminRepository;
    private final BidHistoryJpaRepository bidHistoryRepository;
    private final MinPriceRepository minPriceRepository;
    private final KakaoApiUtil kakaoApiUtil;

    @Transactional
    public void registerAdSlot(AdSlotRegisterRequestDto dto, String imageUrl) {
        Admin admin = adminRepository.findById(dto.adminId())
                .orElseThrow(() -> new RuntimeException("관리자 없음"));

        // 1. 주소 → 위도/경도 변환
        double[] coords = kakaoApiUtil.getCoordinatesFromAddress(dto.address());
        Double locX = coords[0];
        Double locY = coords[1];

        // 2. 광고 자리 저장
        AdSlot adSlot = AdSlot.builder()
                .adSlotName(dto.slotName())
                .description(dto.description())
                .imageUrl(imageUrl)
                .address(dto.address())
                .width(dto.width())
                .height(dto.height())
                .admin(admin)
                .locX(locX) // 경도
                .locY(locY) // 위도
                .build();

        adSlotRepository.save(adSlot);

        // 시간대별 최소가격 저장 (minPriceList가 null이 아닐 때만)
        if (dto.minPriceList() != null) {
            for (MinPriceRegisterRequestDto priceDto : dto.minPriceList()) {
                MinPrice minPrice = MinPrice.builder()
                        .adSlot(adSlot)
                        .startTime(LocalTime.parse(priceDto.startTime())) // "00:00" → LocalTime
                        .endTime(LocalTime.parse(priceDto.endTime()))     // "02:00" → LocalTime
                        .price(priceDto.price())
                        .build();
                minPriceRepository.save(minPrice);
            }
        }
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
                case "입찰 성공": bidStatusFinal = BidStatus.SUCCESS; break;
                case "입찰 실패": bidStatusFinal = BidStatus.FAIL; break;
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
                            adSlot.getAdSlotName(),
                            adSlot.getAddress(),
                            maxBidHistory.getBidMoney(),
                            maxBidHistory.getBidStatus() != null ? maxBidHistory.getBidStatus().ordinal() : null
                    ));
                }
            } else {
                // 입찰 이력 없음 → 시간대별 최소가격 제공
                Optional<Long> minPriceOpt = minPriceRepository.findMinPriceByAdSlot(adSlot);
                if (minPriceOpt.isPresent()) {
                    Long minPrice = minPriceOpt.get();
                    if (price == null || minPrice <= price) {
                        result.add(new AdSlotResponseDto(
                                adSlot.getAdSlotId(),
                                adSlot.getAdSlotName(),
                                adSlot.getAddress(),
                                minPrice, // 입찰 없으면 최저가
                                null      // 입찰 상태 없음
                        ));
                    }
                }
            }
        }
        return result;
    }
}
