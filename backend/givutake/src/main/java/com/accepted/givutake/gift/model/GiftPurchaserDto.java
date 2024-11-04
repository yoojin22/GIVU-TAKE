package com.accepted.givutake.gift.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftPurchaserDto {
    List<Purchaser> purchasers;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Purchaser {
        String name;
        int price;
    }
}

