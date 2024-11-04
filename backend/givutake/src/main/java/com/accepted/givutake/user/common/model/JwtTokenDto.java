package com.accepted.givutake.user.common.model;

import lombok.*;

@Builder
@NoArgsConstructor
@ToString
@Getter
@AllArgsConstructor
public class JwtTokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
