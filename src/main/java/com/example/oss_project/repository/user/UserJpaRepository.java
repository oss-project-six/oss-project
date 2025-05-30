package com.example.oss_project.repository.user;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
