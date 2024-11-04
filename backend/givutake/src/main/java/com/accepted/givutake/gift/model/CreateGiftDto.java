package com.accepted.givutake.gift.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class CreateGiftDto {

    @NotBlank(message = "답례품 이름은 필수 입력 값입니다.")
    private String giftName;

    @NotNull
    private int categoryIdx;

    private String giftContent;

    @NotNull
    private int price;
}
