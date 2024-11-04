package com.accepted.givutake.payment.entity;

import com.accepted.givutake.gift.entity.Gifts;
import com.accepted.givutake.gift.enumType.DeliveryStatus;
import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "Orders")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Orders extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 주문 ID
    @Column(name = "order_idx")
    private Long orderIdx;

    @ManyToOne(targetEntity = Users.class, fetch = FetchType.LAZY)// Casecade 설정 불필요
    @JoinColumn(name = "user_idx", referencedColumnName = "user_idx")// 고객 ID 외래키 설정
    private Users users;

    @ManyToOne(targetEntity = Gifts.class, fetch = FetchType.LAZY) // Casecade 설정 불필요
    @JoinColumn(name = "gift_idx", referencedColumnName = "gift_idx") // 답례품 ID 외래키 설정
    private Gifts gift;

    @Column(name = "payment_method", nullable = false) // 결제 수단
    private String paymentMethod;

    @Column(name = "amount", nullable = false, length = 3) // 주문량
    private int amount;

    @Column(name = "price", nullable = false) // 가격
    private int price;

    @Column(name = "card_number", length = 20)
    private String cardNumber;

    @Enumerated(EnumType.STRING) // 주문상태
    private DeliveryStatus status;
}
