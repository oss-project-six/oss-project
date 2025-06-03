package com.example.oss_project.service.adSlot;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.entity.CvInfo;
import com.example.oss_project.domain.response.adSlot.AdslotInforAdResponseDto;
import com.example.oss_project.domain.response.cvInfo.AttentionRateResponseDto;

import com.example.oss_project.domain.response.cvInfo.CvInfoStayTimeDto;
import com.example.oss_project.domain.response.cvInfo.ExposureScoreResponseDto;
import com.example.oss_project.domain.response.cvInfo.NavigateResponseDto;
import com.example.oss_project.domain.response.cvInfo.StayTimeResponseDto;
import com.example.oss_project.domain.response.cvInfo.ViewCountResponseDto;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryRepository;
import com.example.oss_project.repository.cvInfo.CvInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InforAdSlotService {
    private final AdSlotRepository adSlotRepository;
    private final CvInfoRepository cvInfoRepository;
    private final BidHistoryRepository bidHistoryRepository;

    // 광고 자리 상세 정보 페이지 첫 화면
    public AdslotInforAdResponseDto findBasicInfor(Long adSlotId,String type) {
        AdSlot adSlot = adSlotRepository.findByAdSlotId(adSlotId);
        List<BidHistory> bidHistories = bidHistoryRepository.findByAdSlot(adSlot);
        List<CvInfo> cvInfos = cvInfoRepository.findByAdSlotOrderByTimeStampDesc(adSlot);
        Pair<List<Long>, Long> viewCountResult=null;

        if (cvInfos.isEmpty()) {
            throw new IllegalArgumentException("광고 정보가 담기지 않았습니다.");
        }

        CvInfo latest = cvInfos.get(0);

        // 공통: 최근 7일 범위
        Pair<LocalDateTime, LocalDateTime> weekRange = DateRangeUtil.getRangeByType("week", latest.getTimeStamp());
        // 통행량
        List<CvInfo> viewInfos = cvInfoRepository.findByAdSlotAndTimeStampBetweenOrderByTimeStampDesc(
                adSlot, weekRange.getLeft(), weekRange.getRight());
        if (type.equals("week")) {
            viewCountResult = makeTimeViewCountWithTotal(viewInfos,
                    weekRange.getLeft().toLocalDate(), weekRange.getRight().toLocalDate());
        }
        // 노출 점수
        Pair<List<List<Double>>, Double> exposureResult = makeExposureScoresWithAverage(
                viewInfos, weekRange.getLeft().toLocalDate(), weekRange.getRight().toLocalDate());

        // 공통 : 최근 30일 범위
        Pair<LocalDateTime, LocalDateTime> monthRange = DateRangeUtil.getRangeByType("month", latest.getTimeStamp());
        List<CvInfo> monthInfos = cvInfoRepository.findByAdSlotAndTimeStampBetweenOrderByTimeStampDesc(
                adSlot, monthRange.getLeft(), monthRange.getRight());
        if (type.equals("month")){
           viewCountResult = makeWeeklyGroupedViewCount(monthInfos,
                    monthRange.getLeft().toLocalDate(),monthRange.getRight().toLocalDate());
        }

        // 응시율 (최근 하루)
        Pair<LocalDateTime, LocalDateTime> dayRange = DateRangeUtil.getRangeByType("day", latest.getTimeStamp());
        List<CvInfo> dayInfos = cvInfoRepository.findByAdSlotAndTimeStampBetweenOrderByTimeStampDesc(
                adSlot, dayRange.getLeft(), dayRange.getRight());
        Pair<List<Double>, Double> attentionResult = makeTimeAttentionRatioWithAverage(dayInfos, dayRange.getLeft().toLocalDate());
        Pair<List<CvInfoStayTimeDto>,Double> stayTimeDtos = makeTimeStayWithAverage(dayInfos, dayRange.getLeft().toLocalDate());

        NavigateResponseDto navigateResponseDto = new NavigateResponseDto(
                adSlot.getAdSlotName(),
                makeAvgBidMoney(bidHistories),
                adSlot.getStartDate(),
                adSlot.getStartDate().plusDays(1),
                adSlot.getAdSlotStatus()
        );

        AttentionRateResponseDto attentionRateResponseDto = new AttentionRateResponseDto(
                attentionResult.getRight(),
                attentionResult.getLeft()
        );

        ExposureScoreResponseDto exposureScoreResponseDto = new ExposureScoreResponseDto(
                exposureResult.getLeft(),
                exposureResult.getRight()
        );

        ViewCountResponseDto viewCountResponseDto = new ViewCountResponseDto(
                viewCountResult.getLeft(),
                viewCountResult.getRight()
        );
        StayTimeResponseDto stayTimeResponseDto = new StayTimeResponseDto(
                stayTimeDtos.getRight(),
                stayTimeDtos.getLeft()
        );

        return new AdslotInforAdResponseDto(
                latest.getTimeStamp(),
                navigateResponseDto,
                exposureScoreResponseDto,
                attentionRateResponseDto,
                viewCountResponseDto,
                stayTimeResponseDto
        );
    }

    private Pair<List<CvInfoStayTimeDto>, Double> makeTimeStayWithAverage(
            List<CvInfo> attentionInfos, LocalDate startDate) {

        List<CvInfoStayTimeDto> dtoList = new ArrayList<>();
        double totalMidTime = 0.0;

        for (CvInfo info : attentionInfos) {
            dtoList.add(new CvInfoStayTimeDto(
                    info.getTimeStamp().toLocalTime().toString(),
                    info.getMidTime(),
                    info.getMinTime(),
                    info.getQ1Time(),
                    info.getQ3Time(),
                    info.getMaxTime()
            ));
            totalMidTime += info.getMidTime();
        }

        return Pair.of(dtoList,totalMidTime);
    }

    private Pair<List<Long>, Long> makeWeeklyGroupedViewCount(
            List<CvInfo> cvInfos,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // 4주치 그룹
        Long[] weeklyCounts = new Long[4];
        Arrays.fill(weeklyCounts, 0L);

        long total = 0L;

        for (CvInfo cv : cvInfos) {
            LocalDate date = cv.getTimeStamp().toLocalDate();
            if (date.isBefore(startDate) || date.isAfter(endDate)) continue;

            int dayIndex = (int) ChronoUnit.DAYS.between(startDate, date);
            int weekIndex = dayIndex / 7;

            if (weekIndex < 4) {
                weeklyCounts[weekIndex] += cv.getViewCount();
                total += cv.getViewCount();
            }
        }

        return Pair.of(Arrays.asList(weeklyCounts), total);
    }

    private Pair<List<Long>, Long> makeTimeViewCountWithTotal(
            List<CvInfo> cvInfos,
            LocalDate startDate,
            LocalDate endDate
    ) {
        int days = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        Long[] arr = new Long[days];
        Arrays.fill(arr, 0L);
        long total = 0L;

        for (CvInfo cv : cvInfos) {
            LocalDate cvDate = cv.getTimeStamp().toLocalDate();
            if (cvDate.isBefore(startDate) || cvDate.isAfter(endDate)) continue;

            int index = (int) ChronoUnit.DAYS.between(startDate, cvDate);
            arr[index] += cv.getViewCount();
            total += cv.getViewCount();
        }

        return Pair.of(Arrays.asList(arr), total);
    }

    private Pair<List<Double>, Double> makeTimeAttentionRatioWithAverage(List<CvInfo> cvInfos, LocalDate targetDate) {
        Double[] time = new Double[12];
        Arrays.fill(time, 0.0);
        boolean[] filled = new boolean[12];
        double total = 0.0;
        int count = 0;

        for (CvInfo cv : cvInfos) {
            LocalDateTime ts = cv.getTimeStamp();
            if (!ts.toLocalDate().equals(targetDate)) continue;

            int hour = ts.getHour();
            int index = (hour == 0) ? 11 : (hour / 2) - 1;
            if (index < 0 || index > 11) continue;

            time[index] = cv.getAttentionRatio();
            filled[index] = true;
            total += cv.getAttentionRatio();
            count++;
        }

        List<Double> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            result.add(filled[i] ? time[i] : 0.0);
        }

        double avg = count == 0 ? 0.0 : Math.round((total / count) * 10.0) / 10.0;
        return Pair.of(result, avg);
    }

    private Pair<List<List<Double>>, Double> makeExposureScoresWithAverage(List<CvInfo> cvInfos, LocalDate startDate, LocalDate endDate) {
        double[][] scoreMatrix = new double[7][12];
        boolean[][] filled = new boolean[7][12];
        double totalScore = 0.0;
        int count = 0;

        for (CvInfo cv : cvInfos) {
            LocalDateTime ts = cv.getTimeStamp();
            LocalDate date = ts.toLocalDate();
            if (date.isBefore(startDate) || date.isAfter(endDate)) continue;

            int hour = ts.getHour();
            int timeSlot = (hour == 0) ? 11 : (hour / 2) - 1;
            if (timeSlot < 0 || timeSlot > 11) continue;

            int dayIndex = (int) ChronoUnit.DAYS.between(startDate, date);
            if (dayIndex < 0 || dayIndex >= 7) continue;

            scoreMatrix[dayIndex][timeSlot] = cv.getExposureScore();
            filled[dayIndex][timeSlot] = true;
            totalScore += cv.getExposureScore();
            count++;
        }

        List<List<Double>> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < 12; j++) {
                row.add(filled[i][j] ? scoreMatrix[i][j] : 0.0);
            }
            result.add(row);
        }

        double avg = count == 0 ? 0.0 : Math.round((totalScore / count) * 10.0) / 10.0;
        return Pair.of(result, avg);
    }

    private Long makeAvgBidMoney(List<BidHistory> bidHistories) {
        return Math.round(bidHistories.stream()
                .mapToLong(BidHistory::getBidMoney)
                .average()
                .orElse(0.0)
        );
    }

    public Double makeAvgAttentionRatio(List<CvInfo> cvInfos) {
        return Math.round(cvInfos.stream()
                .mapToDouble(CvInfo::getAttentionRatio)
                .average()
                .orElse(0.0) * 10
        ) / 10.0;
    }

    public Double makeAvgExposureScore(List<CvInfo> cvInfos) {
        return Math.round(
                cvInfos.stream()
                        .mapToDouble(CvInfo::getExposureScore)
                        .average()
                        .orElse(0.0) * 10
        ) / 10.0;
    }
}

