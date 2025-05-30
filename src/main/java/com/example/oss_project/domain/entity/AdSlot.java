package com.example.oss_project.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
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

    @OneToMany(mappedBy = "adSlot", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CvInfo> cvInfos;

    @OneToMany(mappedBy = "adSlot", cascade = CascadeType.ALL)
    private List<MinPrice> minPrices = new ArrayList<>();


    @Column(name = "ad_slot_description")
    private String description;

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
