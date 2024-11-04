package com.accepted.givutake.user.common.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetDto {

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()\\[\\]{}_+=\\-,])[A-Za-z\\d~!@#$%^&*()\\[\\]{}_+=\\-,]{8,16}$", message = "비밀번호 형식이 올바르지 않습니다.")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @Pattern(regexp = "\\d{6}", message = "인증코드는 6자리 숫자여야 합니다.")
    @NotBlank(message = "인증코드는 필수 입력 값 입니다.")
    private String code;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;
}
