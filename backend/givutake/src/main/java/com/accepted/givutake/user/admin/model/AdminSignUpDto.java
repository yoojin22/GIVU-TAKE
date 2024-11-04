package com.accepted.givutake.user.admin.model;

import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.enumType.Roles;
import com.accepted.givutake.user.common.model.LoginDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AdminSignUpDto extends LoginDto {

    @NotBlank(message = "이름은 필수 입력 값 입니다.")
    private String name;

    @NotBlank(message = "관리자 회원가입을 위한 확인 코드는 필수 입력 값 입니다.")
    private String code;

    public Users toEntity(String profileImageUrl) {
        return Users.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .profileImageUrl(profileImageUrl)
                .roles(Roles.ROLE_ADMIN)
                .isSocial(false)
                .isWithdraw(false)
                .build();
    }
}
