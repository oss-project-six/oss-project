package com.example.oss_project.domain.request.auth;

import com.example.oss_project.domain.type.AuthStatus;

public record SignUpRequestDto(
        String id,
        String password,
        String rePassword,
        String nickname,
        AuthStatus authStatus
) {
}
