package com.accepted.givutake.user.common.entity;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailCode {

    private String email;
    private String code;
    private long ttl;
}
