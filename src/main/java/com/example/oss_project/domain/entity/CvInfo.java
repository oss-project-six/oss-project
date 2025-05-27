package com.example.oss_project.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    @OneToOne
    @JoinColumn(name = "ad_slot_id")
    private AdSlot adSlot;

    @Column(name = "cv_info_time_stamp",nullable = false)
    private LocalDateTime timeStamp;

    @Column(name = "cv_info_avg_time",nullable = false)
    private Double avgTime;

    @Column(name = "cv_info_exposure_score",nullable = false)
    private Double exposureScore;

    @Column(name = "cv_info_attention_ration",nullable = false)
    private Double attentionRatio;

    @Column(name = "cv_info_view_count",nullable = false)
    private Long viewCount;
}
