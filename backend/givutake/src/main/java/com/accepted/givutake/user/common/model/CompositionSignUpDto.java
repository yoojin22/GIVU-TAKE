package com.accepted.givutake.user.common.model;

import com.accepted.givutake.user.client.model.AddressSignUpDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class CompositionSignUpDto {

    @NotNull(message = "회원 정보는 필수 입력 값 입니다.")
    @Valid
    private SignUpDto signUpDto;
    private AddressSignUpDto addressSignUpDto;
}
