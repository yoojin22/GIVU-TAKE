package com.accepted.givutake.gift.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftStatisticsDto {
    private GiftYearStatisticsDto giftYearStatisticsDto;
    private GiftPercentageDto giftPercentageDto;
    private GiftPurchaserDto giftPurchaserDto;
}
