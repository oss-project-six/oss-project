package com.example.oss_project.repository.other;

import com.example.oss_project.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
