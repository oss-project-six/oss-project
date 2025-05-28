package com.example.oss_project.domain.request.auth;

public record SignUpRequestDto(
        String id,
        String password,
        String rePassword,
        String nickname
) {
}
