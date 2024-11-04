package com.accepted.givutake.user.admin.model;

import com.accepted.givutake.user.common.entity.Users;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserViewDto {

    private String name;
    private String email;
    private String profileUrl;

    public static AdminUserViewDto toDto(Users users) {
        return AdminUserViewDto.builder()
                .name(users.getName())
                .email(users.getEmail())
                .profileUrl(users.getProfileImageUrl())
                .build();
    }
}
