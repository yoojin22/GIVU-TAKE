package com.accepted.givutake.wish.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateWishDto {

    @NotNull
    private int giftIdx;
}
