package com.example.oss_project.domain.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne(mappedBy = "adSlot", fetch = FetchType.LAZY) // 양방향으로 변경함
    private CvInfo cvInfo;

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
