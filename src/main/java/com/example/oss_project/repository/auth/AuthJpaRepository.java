package com.example.oss_project.repository.auth;

import com.example.oss_project.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
}
