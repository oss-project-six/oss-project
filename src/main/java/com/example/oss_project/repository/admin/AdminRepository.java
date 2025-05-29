package com.example.oss_project.repository.admin;

import com.example.oss_project.domain.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    // 추가적인 쿼리가 필요하면 여기에 메서드 선언
}
