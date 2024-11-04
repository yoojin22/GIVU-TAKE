package com.accepted.givutake.funding.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FundingParticipateDto {
    List<participant> participants;
}
