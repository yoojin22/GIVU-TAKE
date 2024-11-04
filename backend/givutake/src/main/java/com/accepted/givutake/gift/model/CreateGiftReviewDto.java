package com.accepted.givutake.gift.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class CreateGiftReviewDto {

    @NotBlank(message = "답례품 리뷰는 필수 입력 값입니다.")
    private String reviewContent;

    @NotNull
    private int giftIdx;

    @NotNull
    private long orderIdx;
}
