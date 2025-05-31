//package com.example.oss_project.repository.auth;
//
//import com.example.oss_project.core.exception.CustomException;
//import com.example.oss_project.core.exception.ErrorCode;
//import com.example.oss_project.domain.entity.Admin;
//import com.example.oss_project.domain.entity.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//@Repository
//@RequiredArgsConstructor
//public class AuthRepository {
//    private final AuthUserJpaRepository authUserJpaRepository;
//    private final AuthAdminJpaRepository authAdminJpaRepository;
//
//    public User findByUserLoginId(String userLoginId){
//        return authUserJpaRepository.findByLoginId(userLoginId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
//    }
//
//    public Admin findByAdminLoginId(String loginId){
//        return authAdminJpaRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ADMIN));
//    }
//
//    public User save(User user){ return authUserJpaRepository.save(user); }
//    public Admin save(Admin admin){ return authAdminJpaRepository.save(admin); }
//}
