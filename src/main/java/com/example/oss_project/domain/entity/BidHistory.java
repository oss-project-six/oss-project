package com.example.oss_project.domain.entity;

import com.example.oss_project.domain.type.BidStatus;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder(builderMethodName = "bidBuilder")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bid_history")
public class BidHistory extends BaseEntity {
    @Id
    @Column(name = "bid_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_slot_id")
    private AdSlot adSlot;

    @Column(name = "bid_money",nullable = false)
    private Long bidMoney;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "bid_status",nullable = false)
    private BidStatus bidStatus = BidStatus.BEFORE_BIDDING;

    @Column(name = "bid_start_time")
    private LocalDateTime bidStartTime;

    @Column(name = "bid_end_time")
    private LocalDateTime bidEndTime;
}
