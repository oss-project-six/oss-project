package com.example.oss_project.repository.adSlot;

import com.example.oss_project.core.exception.CustomException;
import com.example.oss_project.core.exception.ErrorCode;
import com.example.oss_project.domain.entity.AdSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdSlotRepository {

    private final AdslotJpaRepository adslotJpaRepository;

    public AdSlot findByAdSlotId(Long adSlotId){
        return adslotJpaRepository.findByAdSlotId(adSlotId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ADSLOT));
    }

    public List<AdSlot> findAll(){
        return adslotJpaRepository.findAll();
    }

    public AdSlot save(AdSlot adSlot){
        return adslotJpaRepository.save(adSlot);
    }
}
