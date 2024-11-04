package com.accepted.givutake.user.common.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PasswordDto {

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()\\[\\]{}_+=\\-,])[A-Za-z\\d~!@#$%^&*()\\[\\]{}_+=\\-,]{8,16}$", message = "비밀번호 형식이 올바르지 않습니다.")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
}
