package com.example.oss_project.core.quatz;

import com.example.oss_project.core.exception.CustomException;
import com.example.oss_project.core.exception.ErrorCode;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.type.AdSlotStatus;
import com.example.oss_project.domain.type.BidStatus;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BidCloseJob implements Job {

    public BidCloseJob(){
    }

    @Autowired
    private BidHistoryRepository bidHistoryRepository;
    @Autowired
    private AdSlotRepository adSlotRepository;
    @Autowired
    private Scheduler scheduler;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long adSlotId = context.getMergedJobDataMap().getLong("adSlotId");
        AdSlot adSlot = adSlotRepository.findByAdSlotId(adSlotId);
        List<BidHistory> bidHistories = bidHistoryRepository.findByAdSlotAndTimeStampBetweenOrderByTimeStampDesc(
                adSlot,
                adSlot.getBidStartTime(),
                adSlot.getBidEndTime()
        );

        for (BidHistory bh :bidHistories){
            bh.setBidStatus(BidStatus.valueOf("BIDDING"));
        }
        // 1. adSlot 조회 및 상태 변경
        adSlot.setAdSlotStatus(AdSlotStatus.AD_CONTINUE);

        // 2. 종료 시각 기준으로 (날짜+시간 전체) 그룹핑
        Map<LocalDateTime, List<BidHistory>> grouped =
                bidHistories.stream()
                        .collect(Collectors.groupingBy(BidHistory::getBidEndTime));

        List<BidHistory> updated = new ArrayList<>();

        // 3. 각 종료시간별 그룹마다 낙찰자 선정
        for (Map.Entry<LocalDateTime, List<BidHistory>> entry : grouped.entrySet()) {
            List<BidHistory> timeBidHistories = entry.getValue();

            // 낙찰자 선정: 금액 우선, 같으면 먼저 입찰한 사람
            BidHistory winner = timeBidHistories.stream()
                    .max(Comparator
                            .comparingLong(BidHistory::getBidMoney)
                            .thenComparing(BidHistory::getBidStartTime))
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BIDHISTORY));

            // 입찰 상태 갱신
            for (BidHistory bh : timeBidHistories) {
                if (bh.getBidId().equals(winner.getBidId())) {
                    bh.setBidStatus(BidStatus.SUCCESS);
                } else {
                    bh.setBidStatus(BidStatus.FAIL);
                }
                updated.add(bh);
            }
        }
        // 5. 한 번에 저장
        bidHistoryRepository.saveAll(updated);

        try {
            JobDataMap map = new JobDataMap();
            map.put("adSlotId", adSlotId);
            map.put("bidHistories",bidHistories);

            JobDetail jobDetail = JobBuilder.newJob(AdSlotCloseJob.class)
                    .withIdentity("adSlotCloseJob_" + adSlotId)
                    .usingJobData(map)
                    .storeDurably()
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("adSlotCloseTrigger_" + adSlotId)
                    .startAt(Date.from(Instant.now().plus(Duration.ofMinutes(1))))
                    .forJob(jobDetail)
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }
}
