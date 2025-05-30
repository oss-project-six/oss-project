package com.example.oss_project.repository.cvInfo;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.CvInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CvInfoJpaRepository extends JpaRepository<CvInfo,Long> {
    public List<CvInfo> findByAdSlotOrderByTimeStampDesc(AdSlot adSlot);
    List<CvInfo> findByAdSlot(AdSlot adSlot);
}
