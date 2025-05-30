package com.example.oss_project.repository.adSlot;

import com.example.oss_project.domain.entity.AdSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdslotJpaRepository extends JpaRepository<AdSlot,Long> {
    public Optional<AdSlot> findByAdSlotId(Long adSlotId);
    List<AdSlot> findAll();
    List<AdSlot> findByAdmin_AdminId(Long adminId);
}
