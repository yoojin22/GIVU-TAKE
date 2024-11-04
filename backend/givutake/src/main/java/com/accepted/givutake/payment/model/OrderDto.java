package com.accepted.givutake.payment.model;

import com.accepted.givutake.gift.enumType.DeliveryStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long orderIdx;
    private Integer userIdx;
    private String regionName;
    private Integer giftIdx;
    private String giftName;
    private String giftThumbnail;
    private String paymentMethod;
    private Integer amount;
    private Integer price;
    private Boolean isWrite;
    private DeliveryStatus status;
    private LocalDateTime createdDate;
}
