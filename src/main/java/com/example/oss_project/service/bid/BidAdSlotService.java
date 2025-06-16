package com.example.oss_project.service.bid;

import com.example.oss_project.core.exception.CustomException;
import com.example.oss_project.core.exception.ErrorCode;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.entity.CvInfo;
import com.example.oss_project.domain.response.bidHistory.AdInforBidResponseDto;
import com.example.oss_project.domain.response.bidHistory.BidAdSlotResponseDto;
import com.example.oss_project.domain.response.bidHistory.NavigateBidResponseDto;
import com.example.oss_project.domain.type.BidStatus;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryRepository;
import com.example.oss_project.repository.cvInfo.CvInfoRepository;
import com.example.oss_project.service.adSlot.DateRangeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidAdSlotService {

    private final AdSlotRepository adSlotRepository;
    private final CvInfoRepository cvInfoRepository;
    private final BidHistoryRepository bidHistoryRepository;

    public BidAdSlotResponseDto bidAdSlot(Long adSlotId) {
        AdSlot adSlot = adSlotRepository.findByAdSlotId(adSlotId);
        List<CvInfo> cvInfos = cvInfoRepository.findByAdSlot(adSlot);

        Pair<LocalDateTime, LocalDateTime> monthRange = DateRangeUtil.getRangeByType("month", LocalDateTime.now());
        List<BidHistory> monthBidHistories = bidHistoryRepository.findByAdSlotAndTimeStampBetweenOrderByTimeStampDesc(
                adSlot, monthRange.getLeft(), monthRange.getRight());

        Pair<LocalDateTime, LocalDateTime> dayRange = DateRangeUtil.getRangeByType("day", LocalDateTime.now());
        List<BidHistory> dayBidHistories = bidHistoryRepository.findByAdSlotAndTimeStampBetweenOrderByTimeStampDesc(
                adSlot, dayRange.getLeft(), dayRange.getRight());

        AdInforBidResponseDto adInforBidResponseDto = makeAdInforBidResponse(adSlot, cvInfos, monthBidHistories, dayBidHistories);
        NavigateBidResponseDto navigateBidResponseDto = makeNavigateBidResponse(adSlot);

        return new BidAdSlotResponseDto(navigateBidResponseDto, adInforBidResponseDto);
    }


    private AdInforBidResponseDto makeAdInforBidResponse(
            AdSlot adSlot,
            List<CvInfo> cvInfos,
            List<BidHistory> MonthBidHistories,
            List<BidHistory> dayBidHistories
    ) {

        LocalDateTime baseMonday = LocalDate.now()
                .with(DayOfWeek.MONDAY)
                .atStartOfDay();

        List<Long> avgBidMoneyList = new ArrayList<>();
        List<Long> maxBidMoneyList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            LocalDateTime weekStart = baseMonday.minusWeeks(i);
            LocalDateTime weekEnd = weekStart.plusDays(6).with(LocalTime.of(23, 59, 59));

            List<BidHistory> weekBids = MonthBidHistories.stream()
                    .filter(bh -> {
                        LocalDateTime t = bh.getBidEndTime();
                        return !t.isBefore(weekStart) && !t.isAfter(weekEnd);
                    })
                    .toList();

            long avg = (long) weekBids.stream()
                    .mapToLong(BidHistory::getBidMoney)
                    .average()
                    .orElse(0);

            long max = weekBids.stream()
                    .mapToLong(BidHistory::getBidMoney)
                    .max()
                    .orElse(0);

            avgBidMoneyList.add(avg);
            maxBidMoneyList.add(max);
        }

        int[] hours = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 0};
        List<Long> avgTimeBidMoneyList = new ArrayList<>();

        for (int hour : hours) {
            List<BidHistory> matching = dayBidHistories.stream()
                    .filter(bh -> bh.getBidEndTime().getHour() == hour)
                    .toList();

            long avg = (long) matching.stream()
                    .mapToLong(BidHistory::getBidMoney)
                    .average()
                    .orElse(0);

            avgTimeBidMoneyList.add(avg);
        }

        return new AdInforBidResponseDto(
                avgBidMoneyList,
                maxBidMoneyList,
                avgTimeBidMoneyList
        );
    }


    public NavigateBidResponseDto makeNavigateBidResponse(AdSlot adSlot){
        return new NavigateBidResponseDto(
                adSlot.getAdSlotName(),
                adSlot.getAddress(),
                adSlot.getDescription(),
                adSlot.getWidth(),
                adSlot.getHeight(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(4)
        );
    }
}
