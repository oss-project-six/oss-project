package com.example.oss_project.repository.ad;

import com.example.oss_project.domain.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByUser_UserId(Long userId); // user_id로 광고 전체 조회
    Optional<Ad> findByAdId(Long AdId);
}
