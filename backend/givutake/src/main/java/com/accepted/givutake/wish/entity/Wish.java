package com.accepted.givutake.wish.entity;

import com.accepted.givutake.gift.entity.Gifts;
import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "Wish")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Wish extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 찜 ID
    @Column(name = "wish_idx")
    private int wishIdx;

    @ManyToOne(targetEntity = Gifts.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_idx", referencedColumnName = "gift_idx") // 답례품 외래키 설정
    private Gifts gift;

    @ManyToOne(targetEntity = Users.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", referencedColumnName = "user_idx") // 사용자 외래키 설정
    private Users users;
    
}
