package com.accepted.givutake.wish.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class WishDto {
    private Integer wishIdx;
    private Integer giftIdx;
    private String giftName;
    private String giftThumbnail;
    private Integer userIdx;
}
