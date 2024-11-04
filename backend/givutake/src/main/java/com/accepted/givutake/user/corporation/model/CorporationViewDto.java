package com.accepted.givutake.user.corporation.model;

import com.accepted.givutake.user.common.entity.Users;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CorporationViewDto {

    private String email;
    private String sido;
    private String sigungu;
    private String name;

    public static CorporationViewDto toDto(Users users) {
        return CorporationViewDto.builder()
                .email(users.getEmail())
                .sido(users.getRegion().getSido())
                .sigungu(users.getRegion().getSigungu())
                .name(users.getName())
                .build();
    }
}
