package com.example.oss_project.core.security;

import com.example.oss_project.core.exception.CustomException;
import com.example.oss_project.domain.entity.Admin;
import com.example.oss_project.domain.entity.User;
import com.example.oss_project.repository.auth.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String userLoginId) throws UsernameNotFoundException {
//        User user = authRepository.findByUserLoginId(userLoginId);
//
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getUserLoginId())
//                .password(user.getPassword()) // 암호화된 비밀번호
//                .roles("USER") // 권한 설정
//                .build();

        // user 테이블 먼저 조회
        try {
            User user = authRepository.findByUserLoginId(userLoginId);
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUserLoginId())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        } catch (CustomException e) {
            // admin 테이블에서 조회
            Admin admin = authRepository.findByAdminLoginId(userLoginId);
            return org.springframework.security.core.userdetails.User.builder()
                    .username(admin.getAdminLoginId())
                    .password(admin.getPassword())
                    .roles("ADMIN")
                    .build();
        }
    }
}