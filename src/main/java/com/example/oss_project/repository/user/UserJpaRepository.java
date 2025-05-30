package com.example.oss_project.repository.user;

import com.example.oss_project.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {}
