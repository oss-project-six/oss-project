package com.example.oss_project.repository.cvInfo;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.CvInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CvInfoJpaRepository extends JpaRepository<CvInfo,Long> {
    public List<CvInfo> findByAdSlotOrderByTimeStampDesc(AdSlot adSlot);
    List<CvInfo> findByAdSlot(AdSlot adSlot);
    Optional<CvInfo> findByAdSlotAndTimeStamp(AdSlot adSlot, LocalDateTime timeStamp);
    public List<CvInfo> findByAdSlotAndTimeStampBetweenOrderByTimeStampDesc(
            AdSlot adSlot, LocalDateTime start, LocalDateTime end
    );
    public Optional<CvInfo> findFirstByAdSlotOrderByTimeStampDesc(AdSlot adSlot);
}
