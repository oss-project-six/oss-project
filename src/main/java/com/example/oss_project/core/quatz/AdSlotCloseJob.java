package com.example.oss_project.core.quatz;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.BidHistory;
import com.example.oss_project.domain.type.AdSlotStatus;
import com.example.oss_project.domain.type.BidStatus;
import com.example.oss_project.repository.adSlot.AdSlotRepository;
import com.example.oss_project.repository.bidHistory.BidHistoryRepository;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdSlotCloseJob implements Job {
    public AdSlotCloseJob(){
    }

    @Autowired
    private AdSlotRepository adSlotRepository;
    @Autowired
    private BidHistoryRepository bidHistoryRepository;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        Long adSlotId = dataMap.getLong("adSlotId");
        @SuppressWarnings("unchecked")
        List<BidHistory> bidHistories = (List<BidHistory>) context.getMergedJobDataMap().get("bidHistories");

        AdSlot adSlot = adSlotRepository.findByAdSlotId(adSlotId);

        List<BidHistory> updated = new ArrayList<>();
        for (BidHistory bh : bidHistories){
            bh.setBidStatus(BidStatus.BEFORE_BIDDING);
            updated.add(bh);
        }
        bidHistoryRepository.saveAll(updated);

        adSlot.setAdSlotStatus(AdSlotStatus.BEFORE_BIDDING);
    }
}