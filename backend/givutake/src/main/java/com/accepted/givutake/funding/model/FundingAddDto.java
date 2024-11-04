package com.accepted.givutake.funding.model;

import com.accepted.givutake.funding.ValidFundingAddDtoDates;
import com.accepted.givutake.funding.entity.Fundings;
import com.accepted.givutake.user.common.entity.Users;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidFundingAddDtoDates
public class FundingAddDto {

    @Size(max = 30, message = "펀딩 제목은 최대 30자 입니다.")
    @NotBlank(message = "펀딩 제목은 필수 입력 값 입니다.")
    private String fundingTitle;

    @Size(max = 6000, message = "펀딩 내용은 최대 6000자 입니다.")
    @NotBlank(message = "펀딩 내용은 필수 입력 값 입니다.")
    private String fundingContent;

    @NotNull(message = "목표 금액은 필수 입력 값 입니다.")
    private Integer goalMoney;

    @NotNull(message = "모금 시작일은 필수 입력 값 입니다.")
//    @FutureOrPresent(message = "모금 시작일은 현재 날짜 이후여야 합니다.")
    private LocalDate startDate;

    @NotNull(message = "모금 종료일은 필수 입력 값 입니다.")
//    @FutureOrPresent(message = "모금 종료일은 현재 날짜 이후여야 합니다.")
    private LocalDate endDate;

    @NotNull(message = "펀딩 종류는 필수 입력 값 입니다.")
    private Character fundingType;

    public Fundings toEntity(Users users, byte state, String fundingThumbnail, String fundingContentImage) {
        return Fundings.builder()
                .corporation(users)
                .fundingTitle(this.fundingTitle)
                .fundingContent(this.fundingContent)
                .fundingContentImage(fundingContentImage)
                .goalMoney(this.goalMoney)
                .totalMoney(0)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .fundingThumbnail(fundingThumbnail)
                .fundingType(this.fundingType)
                .isDeleted(false)
                .state(state)
                .build();
    }
}
