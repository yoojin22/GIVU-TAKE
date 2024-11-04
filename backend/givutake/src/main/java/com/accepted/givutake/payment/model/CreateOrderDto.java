package com.accepted.givutake.payment.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class CreateOrderDto {

    @NotNull
    private int giftIdx;

    @NotBlank
    private String paymentMethod;

    @NotNull
    private int amount;

    private String cardNumber;


}
