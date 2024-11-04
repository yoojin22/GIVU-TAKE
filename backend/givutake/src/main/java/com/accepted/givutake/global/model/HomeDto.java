package com.accepted.givutake.global.model;

import com.accepted.givutake.funding.model.FundingViewDto;
import com.accepted.givutake.gift.model.GiftDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class HomeDto {
    private List<GiftDto> top10Gifts;
    private List<GiftDto> recentGifts;
    private List<FundingViewDto> deadlineImminentFundings;
}
