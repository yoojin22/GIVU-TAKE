package com.accepted.givutake.payment.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class CreateParticipateDto {

    @NotNull
    private int fundingIdx;

    @NotBlank
    private String paymentMethod;

    @NotNull
    private int price;

    private String cardNumber;
}
