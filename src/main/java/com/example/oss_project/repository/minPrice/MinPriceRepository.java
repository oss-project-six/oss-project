package com.example.oss_project.repository.minPrice;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.MinPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MinPriceRepository extends JpaRepository<MinPrice, Long> {
    List<MinPrice> findByAdSlot(AdSlot adSlot);
}

