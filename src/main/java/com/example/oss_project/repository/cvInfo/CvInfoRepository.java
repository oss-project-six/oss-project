package com.example.oss_project.repository.cvInfo;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.CvInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CvInfoRepository {

    private final CvInfoJpaRepository cvInfoJpaRepository;

    public List<CvInfo> findByAdSlotOrderByTimeStampDesc(AdSlot adSlot){
        return cvInfoJpaRepository.findByAdSlotOrderByTimeStampDesc(adSlot);
    }

    public List<CvInfo> findByAdSlot(AdSlot adSlot){
        return cvInfoJpaRepository.findByAdSlot(adSlot);
    }
}
