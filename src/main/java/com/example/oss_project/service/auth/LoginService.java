//package com.example.oss_project.service.auth;
//
//import com.example.oss_project.core.security.CustomAdminDetailsService;
//import com.example.oss_project.core.security.CustomUserDetailsService;
//import com.example.oss_project.core.security.JwtTokenDto;
//import com.example.oss_project.core.security.JwtTokenProvider;
//import com.example.oss_project.domain.request.auth.LoginRequestDto;
//import com.example.oss_project.domain.type.AuthStatus;
//import com.example.oss_project.repository.auth.AuthRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class LoginService {
//
//    private final AuthRepository authRepository;
//    private final JwtTokenProvider jwtTokenProvider;
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
//    private final CustomUserDetailsService customUserDetailsService;
//    private final CustomAdminDetailsService customAdminDetailsService;
//
//    public JwtTokenDto login(LoginRequestDto loginRequestDto){
//        UserDetails userDetails;
//
//        if (loginRequestDto.authStatus() == AuthStatus.ADMIN) {
//            userDetails = customAdminDetailsService.loadUserByUsername(loginRequestDto.loginId());
//        } else {
//            userDetails = customUserDetailsService.loadUserByUsername(loginRequestDto.loginId());
//        }
//
//        UsernamePasswordAuthenticationToken authenticationToken = new
//                UsernamePasswordAuthenticationToken(loginRequestDto.loginId(), loginRequestDto.password());
//
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//
//        JwtTokenDto jwtToken = jwtTokenProvider.generateToken(authentication);
//
//        return jwtToken;
//    }
//}
