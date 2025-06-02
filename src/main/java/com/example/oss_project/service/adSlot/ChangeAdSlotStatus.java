package com.example.oss_project.service.adSlot;

import com.example.oss_project.domain.entity.AdSlot;
import com.example.oss_project.domain.type.AdSlotStatus;
import com.example.oss_project.repository.adSlot.AdslotJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeAdSlotStatus {
    private final AdslotJpaRepository adSlotRepository;

    @Transactional
    public void changeStatusToContinue(Long adSlotId) {
        AdSlot adSlot = adSlotRepository.findById(adSlotId)
                .orElseThrow(() -> new RuntimeException("광고 자리를 찾을 수 없습니다."));

        // 입찰 전 상태에서만 진행중으로 변경
        if (adSlot.getAdSlotStatus() == AdSlotStatus.FINISH) {
            adSlot.setAdSlotStatus(AdSlotStatus.CONTINUE);
        } else {
            throw new IllegalStateException("현재 상태에서는 입찰 중으로 변경할 수 없습니다.");
        }
    }
}
