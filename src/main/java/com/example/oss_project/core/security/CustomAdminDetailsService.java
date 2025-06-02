package com.example.oss_project.core.security;

import com.example.oss_project.domain.entity.Admin;
import com.example.oss_project.domain.entity.User;
import com.example.oss_project.repository.auth.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
public class CustomAdminDetailsService implements UserDetailsService {
    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String adminLoginId) throws UsernameNotFoundException {
        Admin admin = authRepository.findByAdminLoginId(adminLoginId);

        return org.springframework.security.core.userdetails.User.builder()
                .username(admin.getAdminLoginId())
                .password(admin.getPassword()) // 암호화된 비밀번호
                .roles("ADMIN") // 권한 설정
                .build();
    }
}
