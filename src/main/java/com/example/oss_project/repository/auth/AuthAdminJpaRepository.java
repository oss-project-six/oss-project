package com.example.oss_project.repository.auth;

import com.example.oss_project.domain.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthAdminJpaRepository extends JpaRepository<Admin, Long> {
    public Optional<Admin> findByAdminLoginId(String loginId);
}
