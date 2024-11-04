package com.accepted.givutake.gift.entity;

import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.payment.entity.Orders;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Table(name="Gift_reviews")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiftReviews extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 후기 ID
    @Column(name = "review_idx")
    private int reviewIdx;

    @Column(name = "review_image", length = 2048) // 후기 사진
    private String reviewImage;

    @Column(name = "review_content", nullable = false, length = 6000) // 후기 내용
    private String reviewContent;

    @ManyToOne(targetEntity = Gifts.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_idx", referencedColumnName = "gift_idx") // 답례품 ID 외래키 설정
    private Gifts gifts;

    @ManyToOne(targetEntity = Users.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", referencedColumnName = "user_idx")// 회원 ID 외래키 설정
    private Users users;

    @ManyToOne(targetEntity = Orders.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_idx", referencedColumnName = "order_idx")
    private Orders orders;

    @Builder.Default
    @Column(name = "is_delete")
    private boolean isDelete = false;

    @Builder.Default
    @Column(name = "liked_count")
    private int likedCount = 0;
}
