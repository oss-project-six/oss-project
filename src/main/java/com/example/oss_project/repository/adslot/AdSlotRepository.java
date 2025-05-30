package com.example.oss_project.repository.adslot;

import com.example.oss_project.domain.entity.AdSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdSlotRepository extends JpaRepository<AdSlot, Long> {
    List<AdSlot> findAll();
    List<AdSlot> findByAdmin_AdminId(Long adminId);

}
