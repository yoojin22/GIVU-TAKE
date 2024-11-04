package com.accepted.givutake.user.common.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailCodeDto {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 값 입니다.")
    private String email;

    @Pattern(regexp = "\\d{6}", message = "인증코드는 6자리 숫자여야 합니다.")
    @NotBlank(message = "인증코드는 필수 입력 값 입니다.")
    private String code;

}
