package com.example.oss_project.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cv_info")
public class CvInfo {
    @Id
    @Column(name = "cv_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cvInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_slot_id")
    private AdSlot adSlot;

    @Column(name = "cv_info_time_stamp",nullable = false)
    private LocalDateTime timeStamp;

    @Column(name = "cv_info_mid_time",nullable = false)
    private Double midTime;

    @Column(name = "cv_info_exposure_score",nullable = false)
    private Double exposureScore;

    @Column(name = "cv_info_attention_ration",nullable = false)
    private Double attentionRatio;

    @Column(name = "cv_info_view_count",nullable = false)
    private Long viewCount;
}
