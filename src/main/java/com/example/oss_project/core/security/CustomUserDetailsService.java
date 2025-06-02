package com.example.oss_project.core.security;

import com.example.oss_project.core.exception.CustomException;
import com.example.oss_project.domain.entity.Admin;
import com.example.oss_project.domain.entity.User;
import com.example.oss_project.repository.auth.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            User user = authRepository.findByUserLoginId(userId);
            return new CustomUserDetails(
                    user.getUserId(),                    // ✅ getUserId() 직접 저장
                    user.getUserLoginId(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority("USER"))
            );
        } catch (CustomException e) {
            Admin admin = authRepository.findByAdminLoginId(userId);
            return new CustomUserDetails(
                    admin.getAdminId(),
                    admin.getAdminLoginId(),
                    admin.getPassword(),
                    List.of(new SimpleGrantedAuthority("ADMIN"))
            );
        }
    }


}