package com.accepted.givutake.user.client.model;

import com.accepted.givutake.user.client.entity.Cards;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CardViewDto {

    private int cardIdx;
    private String cardCompany;
    private String cardNumber;
    private String cardCVC;
    private LocalDate cardExpiredDate;
    private boolean isRepresentative;

    public static CardViewDto toDto(Cards cards) {
        return CardViewDto.builder()
                .cardIdx(cards.getCardIdx())
                .cardCompany(cards.getCardCompany())
                .cardNumber(cards.getCardNumber())
                .cardCVC(cards.getCardCVC())
                .cardExpiredDate(cards.getCardExpiredDate())
                .isRepresentative(cards.isRepresentative())
                .build();
    }
}
