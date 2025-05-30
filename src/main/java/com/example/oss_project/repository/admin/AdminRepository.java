package com.example.oss_project.repository.admin;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
