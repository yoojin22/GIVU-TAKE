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
public class CorporationDetailViewDto extends CorporationViewDto {

    private String mobilePhone;
    private String landlinePhone;
    private String profileImageUrl;

    public static CorporationDetailViewDto toDto(Users users) {
        return CorporationDetailViewDto.builder()
                .email(users.getEmail())
                .name(users.getName())
                .mobilePhone(users.getMobilePhone())
                .landlinePhone(users.getLandlinePhone())
                .sido(users.getRegion().getSido())
                .sigungu(users.getRegion().getSigungu())
                .profileImageUrl(users.getProfileImageUrl())
                .build();
    }
}
