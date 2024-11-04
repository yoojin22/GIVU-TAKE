package com.accepted.givutake.payment.entity;

import com.accepted.givutake.funding.entity.Fundings;
import com.accepted.givutake.global.entity.BaseTimeEntity;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Table(name="funding_participants")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingParticipants extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_idx", nullable = false)
    private long participantIdx;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "funding_idx", nullable = false)
    private Fundings fundings;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_idx", nullable = false)
    private Users users;

    @Column(name = "funding_fee", nullable = false)
    private int fundingFee;

    @Column(name = "payment_method", nullable = false) // 결제 수단
    private String paymentMethod;

    @Column(name = "card_number", length = 20)
    private String cardNumber;

}
