package com.example.oss_project.domain.request.auth;

import lombok.Getter;

public record LoginRequestDto(
        String loginId,
        String password
) {
}
