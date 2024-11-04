package com.accepted.givutake.gift.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateGiftReviewDto {

    @NotBlank(message = "답례품 리뷰는 필수 입력 값입니다.")
    private String reviewContent;

}
