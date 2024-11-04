package com.accepted.givutake.payment.model;

import com.accepted.givutake.gift.enumType.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOrderDto {

    @NotNull
    private DeliveryStatus status;

}
