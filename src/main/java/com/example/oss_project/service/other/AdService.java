package com.example.oss_project.service.other;

import com.example.oss_project.domain.entity.Ad;
import com.example.oss_project.domain.entity.User;
import com.example.oss_project.repository.other.AdRepository;
import com.example.oss_project.repository.other.UserRepository;
import com.example.oss_project.domain.dto.AdRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdService {
    private final AdRepository adRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerAd(AdRegisterRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Ad ad = new Ad(
                null,               // adId: 보통 auto-generated라 null 또는 0
                user,
                dto.getName(),
                dto.getImageUrl()
        );

        adRepository.save(ad);
    }

}