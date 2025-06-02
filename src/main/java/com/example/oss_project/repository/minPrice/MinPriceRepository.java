package com.example.oss_project.repository.minPrice;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.MinPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MinPriceRepository extends JpaRepository<MinPrice, Long> {
    List<MinPrice> findByAdSlot(AdSlot adSlot);
    @Query("SELECT MIN(m.price) FROM MinPrice m WHERE m.adSlot = :adSlot")
    Optional<Long> findMinPriceByAdSlot(@Param("adSlot") AdSlot adSlot);

}

