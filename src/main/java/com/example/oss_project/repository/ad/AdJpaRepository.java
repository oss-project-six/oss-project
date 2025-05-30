package com.example.oss_project.repository.ad;

import com.example.oss_project.domain.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdJpaRepository extends JpaRepository<Ad, Long> {
    public Optional<Ad> findByAdId(Long adId);
}
