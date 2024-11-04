package com.accepted.givutake.region.entity;

import com.accepted.givutake.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name="Region")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Region extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_idx", nullable = false)
    private int regionIdx;

    @Column(name = "sido", nullable = false, length = 10)
    private String sido;

    @Column(name = "sigungu", nullable = false, length = 10)
    private String sigungu;
}