package com.accepted.givutake.funding.model;

import com.accepted.givutake.funding.entity.Fundings;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FundingViewDto {

    protected int fundingIdx;
    protected String sido;
    protected String sigungu;
    protected String fundingTitle;
    protected int goalMoney;
    protected int totalMoney;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected String fundingThumbnail;
    protected char fundingType;

    public static FundingViewDto toDto(Fundings fundings) {
        return FundingViewDto.builder()
                .fundingIdx(fundings.getFundingIdx())
                .sido(fundings.getCorporation().getRegion().getSido())
                .sigungu(fundings.getCorporation().getRegion().getSigungu())
                .fundingTitle(fundings.getFundingTitle())
                .goalMoney(fundings.getGoalMoney())
                .totalMoney(fundings.getTotalMoney())
                .startDate(fundings.getStartDate())
                .endDate(fundings.getEndDate())
                .fundingThumbnail(fundings.getFundingThumbnail())
                .fundingType(fundings.getFundingType())
                .build();
    }
}
