package com.accepted.givutake.funding.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingReviewUpdateDto {

    @Size(max = 6000)
    @NotNull(message = "내용은 필수 입력 값 입니다.")
    private String reviewContent;
}
