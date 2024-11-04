package com.accepted.givutake.cart.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCartDto {

    @NotNull
    private int amount;
}
