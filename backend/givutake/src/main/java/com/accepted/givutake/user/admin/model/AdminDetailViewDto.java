package com.accepted.givutake.user.admin.model;

import com.accepted.givutake.user.common.entity.Users;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDetailViewDto {

    private String email;
    private String name;
    private String profileImageUrl;

    public static AdminDetailViewDto toDto(Users users) {
        return AdminDetailViewDto.builder()
                .email(users.getEmail())
                .name(users.getName())
                .profileImageUrl(users.getProfileImageUrl())
                .build();
    }
}
