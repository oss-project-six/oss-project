package com.example.oss_project.repository.auth;

import com.example.oss_project.core.exception.CustomException;
import com.example.oss_project.core.exception.ErrorCode;
import com.example.oss_project.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthRepository {
    private final AuthJpaRepository authJpaRepository;

    public User findByLoginId(String loginId){
        return authJpaRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    public User save(User user){ return authJpaRepository.save(user); }
}
