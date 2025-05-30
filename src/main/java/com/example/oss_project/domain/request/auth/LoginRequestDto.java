package com.example.oss_project.domain.request.auth;

import com.example.oss_project.domain.type.AuthStatus;
import lombok.Getter;

public record LoginRequestDto(
        String loginId,
        String password,
        AuthStatus authStatus
) {
}
