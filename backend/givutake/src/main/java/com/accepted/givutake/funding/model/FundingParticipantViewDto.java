package com.accepted.givutake.funding.model;

import com.accepted.givutake.funding.entity.Fundings;
import com.accepted.givutake.payment.entity.FundingParticipants;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingParticipantViewDto {

    private long participateIdx;
    private int fundingIdx;
    private String fundingThumbnail;
    private String fundingTitle;
    private int fundingFee;
    private char fundingType;
    private LocalDateTime createdDate;

    public static FundingParticipantViewDto toDto(FundingParticipants fundingParticipants) {
        Fundings fundings = fundingParticipants.getFundings();
        return FundingParticipantViewDto.builder()
                .participateIdx(fundingParticipants.getParticipantIdx())
                .fundingIdx(fundings.getFundingIdx())
                .fundingThumbnail(fundings.getFundingThumbnail())
                .fundingTitle(fundings.getFundingTitle())
                .fundingFee(fundingParticipants.getFundingFee())
                .fundingType(fundings.getFundingType())
                .createdDate(fundingParticipants.getCreatedDate())
                .build();
    }
}
