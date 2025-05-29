package com.example.oss_project.domain.request.ad;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdRegisterRequestDto {
    private Long userId;
    private String name;
    private String description; // 새로 추가
    private String imageUrl;
    private String category;   // 새로 추가
}
