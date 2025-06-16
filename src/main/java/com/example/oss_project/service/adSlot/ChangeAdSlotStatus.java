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
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
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

        if (adSlot.getAdSlotStatus() == AdSlotStatus.BID_CONTINUE) {
            throw new IllegalStateException("현재 상태에서는 입찰 중으로 변경할 수 없습니다.");
        }

        // 상태 변경
        adSlot.setBidTime();
        adSlot.setAdSlotStatus(AdSlotStatus.BID_CONTINUE);

        // Quartz 식별자
        JobKey jobKey     = JobKey.jobKey("bidCloseJob_" + adSlotId);
        TriggerKey triggerKey = TriggerKey.triggerKey("bidCloseTrigger_" + adSlotId);

        try {
            // 이미 같은 키의 잡이 있으면 삭제
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }

            // JobDetail 생성
            JobDetail jobDetail = JobBuilder.newJob(BidCloseJob.class)
                    .withIdentity(jobKey)
                    .usingJobData("adSlotId", adSlotId)
                    .storeDurably()
                    .build();

            // Trigger 생성 (1분 뒤 실행)
            Date startTime = Date.from(Instant.now().plus(Duration.ofMinutes(4)));
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .forJob(jobDetail)
                    .startAt(startTime)
                    .build();

            // 스케줄 등록
            scheduler.scheduleJob(jobDetail, trigger);

        } catch (Exception e){
            throw new CustomException(ErrorCode.NOT_ACESS_SCHEDULER);
        }
    }
}
