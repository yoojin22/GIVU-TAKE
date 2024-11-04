package com.accepted.givutake.global.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ExceptionDto {

    @Builder.Default
    private final boolean success = false;
    private String code;
    private String message;

}