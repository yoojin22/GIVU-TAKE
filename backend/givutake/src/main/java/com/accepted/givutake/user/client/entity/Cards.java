package com.accepted.givutake.user.client.entity;

import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@Table(name="Cards")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cards extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_idx", nullable = false)
    private int cardIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private Users users;

    @Column(name = "card_company", nullable = false, length = 30)
    private String cardCompany;

    @Column(name = "card_number", nullable = false, length = 19)
    private String cardNumber;

    @Column(name = "card_cvc", nullable = false, length = 3)
    private String cardCVC;

    @Column(name = "card_expired_date", nullable = false)
    private LocalDate cardExpiredDate;

    @Column(name = "card_password", nullable = false, length = 60)
    private String cardPassword;

    @Column(name = "is_representative", nullable = false)
    private boolean isRepresentative;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
