package com.example.oss_project.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "adSlot")
public class AdSlot {
    @Id
    @Column(name = "ad_slot_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adSlotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(name = "ad_local_name", nullable = false)
    private String localName;

    @Column(name = "ad_image_url")
    private String imageUrl;

    @Column(name = "address")
    private String address;

    @Column(name = "size")
    private String size;

    @Column(name = "min_price")
    private Long minPrice;

    @Column(name = "ad_loc_x")
    private Double locX;

    @Column(name = "ad_loc_y")
    private Double locY;
}
