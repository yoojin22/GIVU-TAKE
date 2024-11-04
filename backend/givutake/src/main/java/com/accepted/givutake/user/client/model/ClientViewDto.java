package com.accepted.givutake.user.client.model;

import com.accepted.givutake.user.common.entity.Users;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientViewDto {

    private String email;
    private String name;
    private String mobilePhone;
    private String landlinePhone;
    private Boolean isMale;
    private LocalDate birth;
    private String profileImageUrl;

    public static ClientViewDto toDto(Users users) {
        return ClientViewDto.builder()
                .email(users.getEmail())
                .name(users.getName())
                .mobilePhone(users.getMobilePhone())
                .landlinePhone(users.getLandlinePhone())
                .isMale(users.getIsMale())
                .birth(users.getBirth())
                .profileImageUrl(users.getProfileImageUrl())
                .build();
    }
}
