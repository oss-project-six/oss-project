package com.example.oss_project.controller;

import com.example.oss_project.core.common.CommonResponseDto;
import com.example.oss_project.core.exception.CustomException;
import com.example.oss_project.core.exception.ErrorCode;
import com.example.oss_project.domain.request.auth.LoginRequestDto;
import com.example.oss_project.domain.request.auth.SignUpRequestDto;
import com.example.oss_project.service.auth.LoginService;
import com.example.oss_project.service.auth.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final SignUpService signUpService;

    @PostMapping("")
    public CommonResponseDto<?> userLogin(
            @RequestBody LoginRequestDto loginRequestDto
    ) {
        try {
            return CommonResponseDto.ok(loginService.login(loginRequestDto));
        } catch (Exception e) {
            // 예외 처리 로직 작성 (예: 에러 응답 반환)
            return CommonResponseDto.fail(new CustomException(ErrorCode.NOT_FOUND_USER));
        }
    }


    @PostMapping("/signup")
    public CommonResponseDto<?> userSignUp(
            @RequestBody SignUpRequestDto signUpRequestDto
    ){
        return CommonResponseDto.ok(signUpService.signUp(signUpRequestDto));
    }
}
