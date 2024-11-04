package com.accepted.givutake.user.client.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressModifyDto {

    @NotNull(message = "대표 주소 여부는 필수 입력 값 입니다.")
    private Boolean isRepresentative;
}
