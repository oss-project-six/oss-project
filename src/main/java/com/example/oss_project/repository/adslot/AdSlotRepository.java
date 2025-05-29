package com.example.oss_project.repository.adslot;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.type.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdSlotRepository extends JpaRepository<AdSlot, Long> {
    List<AdSlot> findAll();

}
