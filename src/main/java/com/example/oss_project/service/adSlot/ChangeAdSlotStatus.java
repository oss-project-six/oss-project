package com.example.oss_project.service.adSlot;

import com.example.oss_project.core.exception.CustomException;
import com.example.oss_project.core.exception.ErrorCode;
import com.example.oss_project.core.quatz.BidCloseJob;
import com.example.oss_project.domain.entity.Ad;
import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.type.AdSlotStatus;
import com.example.oss_project.domain.type.BidStatus;
import com.example.oss_project.repository.ad.AdRepository;
import com.example.oss_project.repository.adSlot.AdslotJpaRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeAdSlotStatus {
    private final AdslotJpaRepository adSlotRepository;
    private final AdRepository adRepository;
    private final BidHistoryRepository bidHistoryRepository;
    private final Scheduler scheduler;

    @Transactional
    public void changeStatusToContinue(Long adSlotId) {
        AdSlot adSlot = adSlotRepository.findById(adSlotId)
                .orElseThrow(() -> new RuntimeException("광고 자리를 찾을 수 없습니다."));

        // 입찰 전 상태에서만 진행중으로 변경
        if (adSlot.getAdSlotStatus() == AdSlotStatus.BID_CONTINUE) {
            throw new IllegalStateException("현재 상태에서는 입찰 중으로 변경할 수 없습니다.");
        }

        adSlot.setBidTime();
        adSlot.setAdSlotStatus(AdSlotStatus.BID_CONTINUE);
        List<BidHistory> bidHistories = bidHistoryRepository.findByAdSlotAndTimeStampBetweenOrderByTimeStampDesc(
                adSlot,
                adSlot.getBidStartTime(),
                adSlot.getBidEndTime()
        );

        for (BidHistory bh :bidHistories){
            bh.setBidStatus(BidStatus.valueOf("BIDDING"));
        }

        try {
            JobDataMap map = new JobDataMap();
            map.put("adSlotId", adSlotId);
            map.put("bidHistories",bidHistories);

            JobDetail jobDetail = JobBuilder.newJob(BidCloseJob.class)
                    .withIdentity("bidCloseJob_" + adSlotId)
                    .usingJobData(map)
                    .storeDurably()
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("bidCloseTrigger_" + adSlotId)
                    .startAt(Date.from(Instant.now().plus(Duration.ofMinutes(1)))) // 입찰 종료 시점에 실행
                    .forJob(jobDetail)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e){
            throw new CustomException(ErrorCode.NOT_ACESS_SCHEDULER);
        }
    }
}
