package com.example.oss_project.repository.cvinfo;

import com.example.oss_project.domain.entity.CvInfo;
import com.example.oss_project.domain.entity.AdSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CvInfoRepository extends JpaRepository<CvInfo, Long> {
    // 광고 자리에 연동된 CvInfo 1개 찾기
    List<CvInfo> findByAdSlot(AdSlot adSlot);
}
