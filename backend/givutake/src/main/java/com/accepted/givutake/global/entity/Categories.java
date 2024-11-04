package com.accepted.givutake.global.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "Categories")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Categories extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 카테고리 ID
    @Column(name = "category_idx")
    private int categoryIdx;

    @Column(name = "category_name", nullable = false, length = 30) // 카테고리 명
    private String categoryName;

    @Column(name = "category_type", nullable = false, length = 1) // 카테고리 타입
    private int categoryType; // 0: 답례품 1: 펀딩
}
