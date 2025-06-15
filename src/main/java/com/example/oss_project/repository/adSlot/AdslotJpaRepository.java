package com.example.oss_project.repository.adSlot;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.CvInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdslotJpaRepository extends JpaRepository<AdSlot,Long> {
    public Optional<AdSlot> findByAdSlotId(Long adSlotId);

    List<AdSlot> findAll();

    @Query("SELECT DISTINCT a FROM AdSlot a LEFT JOIN FETCH a.cvInfos WHERE a.admin.adminId = :adminId")
    List<AdSlot> findByAdmin_AdminIdWithCvInfos(@Param("adminId") Long adminId);
}
