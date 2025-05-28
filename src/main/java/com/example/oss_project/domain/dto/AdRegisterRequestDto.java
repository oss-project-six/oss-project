package com.example.oss_project.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdRegisterRequestDto {
    private Long userId;
    private String name;
    private String imageUrl;
}
