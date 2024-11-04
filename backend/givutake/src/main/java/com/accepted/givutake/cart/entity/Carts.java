package com.accepted.givutake.cart.entity;

import com.accepted.givutake.gift.entity.Gifts;
import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "carts")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Carts extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_idx") // 장바구니 ID
    private int cartIdx;

    @ManyToOne(targetEntity = Gifts.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_idx", referencedColumnName = "gift_idx") // 답례품 외래키 설정
    private Gifts gifts;

    @ManyToOne(targetEntity = Users.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", referencedColumnName = "user_idx")
    private Users users;

    @Column(name = "amount")
    private int amount;

}
