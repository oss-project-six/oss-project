package com.example.oss_project.service.other;

import com.example.oss_project.domain.dto.AdSummaryResponseDto;
import com.example.oss_project.domain.entity.Ad;
import com.example.oss_project.domain.entity.User;
import com.example.oss_project.repository.other.AdRepository;
import com.example.oss_project.repository.other.UserRepository;
import com.example.oss_project.domain.dto.AdRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
                null,
                user,
                dto.getName(),
                dto.getImageUrl()
        );

        adRepository.save(ad);
    }

    public List<AdSummaryResponseDto> getAdsByUserId(Long userId) {
        List<Ad> ads = adRepository.findByUser_UserId(userId);
        return ads.stream()
                .map(ad -> new AdSummaryResponseDto(
                        ad.getName(),
                        ad.getImageUrl()
                        // 추가하고 싶은 특성 추가하여 광고 목록 조회에서 해당 특성 추가 가능
                ))
                .collect(Collectors.toList());
    }

}