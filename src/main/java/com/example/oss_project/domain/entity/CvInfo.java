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

    // 몇시 데이터인지
    @Column(name = "cv_info_time_stamp",nullable = false)
    private LocalDateTime timeStamp;

    // 체류시간 상자그림
    @Column(name = "cv_info_mid_time",nullable = false)
    private Double midTime;
    @Column(name = "cv_info_min_time",nullable = false)
    private Double minTime;
    @Column(name = "cv_info_q1_time",nullable = false)
    private Double q1Time;
    @Column(name = "cv_info_q3_time",nullable = false)
    private Double q3Time;
    @Column(name = "cv_info_max_time",nullable = false)
    private Double maxTime;

    //노출점수
    @Column(name = "cv_info_exposure_score",nullable = false)
    private Double exposureScore;

    //응시율
    @Column(name = "cv_info_attention_ration",nullable = false)
    private Double attentionRatio;

    //통행량
    @Column(name = "cv_info_view_count",nullable = false)
    private Long viewCount;
}
