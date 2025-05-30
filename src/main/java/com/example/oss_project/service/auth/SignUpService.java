package com.example.oss_project.service.auth;

import com.example.oss_project.core.security.SecurityConfig;
import com.example.oss_project.domain.entity.Admin;
import com.example.oss_project.domain.entity.User;
import com.example.oss_project.domain.request.auth.SignUpRequestDto;
import com.example.oss_project.domain.type.AuthStatus;
import com.example.oss_project.repository.auth.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class SignUpService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public Boolean signUp(SignUpRequestDto signUpRequestDto){

        if(!signUpRequestDto.password().equals(signUpRequestDto.rePassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if(signUpRequestDto.authStatus() == AuthStatus.USER){
            User user = User.builder()
                    .userLoginId(signUpRequestDto.id())
                    .nickname(signUpRequestDto.nickname())
                    .password(passwordEncoder.encode(signUpRequestDto.password()))
                    .build();

            authRepository.save(user);
        } else {
            Admin admin = Admin.builder()
                    .adminLoginId(signUpRequestDto.id())
                    .nickname(signUpRequestDto.nickname())
                    .password(passwordEncoder.encode(signUpRequestDto.password()))
                    .build();
            authRepository.save(admin);
        }

        return true;
    }
}
