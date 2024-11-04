package com.accepted.givutake.gift.entity;

import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Table(name="Gift_review_liked")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiftReviewLiked extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 좋아요 ID
    @Column(name = "like_idx")
    private Long likeIdx;

    @ManyToOne(targetEntity = GiftReviews.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "review_idx", referencedColumnName = "review_idx")// 후기 ID 외래키 설정
    private GiftReviews giftReviews;

    @OneToOne(targetEntity = Users.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", referencedColumnName = "user_idx") // 회원 ID 외래키 설정
    private Users user;
}
