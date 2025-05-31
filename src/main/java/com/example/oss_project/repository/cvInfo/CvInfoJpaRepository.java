package com.example.oss_project.repository.cvInfo;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.CvInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CvInfoJpaRepository extends JpaRepository<CvInfo,Long> {
    List<CvInfo> findByAdSlot(AdSlot adSlot);
    Optional<CvInfo> findByAdSlotAndTimeStamp(AdSlot adSlot, LocalDateTime timeStamp);
}
