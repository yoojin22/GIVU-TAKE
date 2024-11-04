package com.accepted.givutake.user.common.entity;


import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private String email;
    private String refreshToken;
    private long ttl;
}
