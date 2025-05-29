package com.example.oss_project.repository.other;

import com.example.oss_project.domain.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByUser_UserId(Long userId); // user_id로 광고 전체 조회
}
