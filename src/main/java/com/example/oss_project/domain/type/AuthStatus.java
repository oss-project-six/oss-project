package com.example.oss_project.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AuthStatus {
    ADMIN("매체사"),
    USER("광고사");

    private final String authStatus;
}
