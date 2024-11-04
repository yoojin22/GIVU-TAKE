package com.accepted.givutake.user.admin.model;

import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.enumType.Roles;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCorporationViewDto {

    private String email;
    private String sido;
    private String sigungu;
    private String name;
    private Roles roles;

    public static AdminCorporationViewDto toDto(Users users) {
        return AdminCorporationViewDto.builder()
                .email(users.getEmail())
                .sido(users.getRegion().getSido())
                .sigungu(users.getRegion().getSigungu())
                .name(users.getName())
                .roles(users.getRoles())
                .build();
    }
}
