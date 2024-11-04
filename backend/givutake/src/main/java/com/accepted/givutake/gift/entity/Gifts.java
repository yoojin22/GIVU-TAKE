package com.accepted.givutake.gift.entity;

import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.global.entity.Categories;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "gifts")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Gifts extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 답례품 ID
    @Column(name = "gift_idx")
    private int giftIdx;

    @Column(name = "gift_name", nullable = false, length = 60) // 답례품명
    private String giftName;

    @ManyToOne(targetEntity = Users.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "corporation_idx", referencedColumnName = "user_idx")// 지자체 ID 외래키 설정
    private Users corporations;

    @ManyToOne(targetEntity = Categories.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_idx", referencedColumnName = "category_idx") // 카테고리 ID 외래키 설정
    private Categories category;

    @Column(name = "gift_thumbnail", length = 2048) // 답례품 썸네일
    private String giftThumbnail; // 기본 이미지 설정

    @Column(name = "gift_content_image", length = 2048)
    private String giftContentImage;

    @Column(name = "gift_content", length = 6000) // 답례품 설명
    private String giftContent;

    @Column(name = "price", nullable = false)
    private int price;

    @Builder.Default
    @Column(name = "is_delete")
    private boolean isDelete = false;

    @Builder.Default
    @Column(name = "amount")
    private int amount = 0;

}
