package com.accepted.givutake.global.exception;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import lombok.Getter;

@Getter
public class JwtAuthenticationException extends RuntimeException {

    private final ExceptionEnum error;

    public JwtAuthenticationException(ExceptionEnum e) {
        super(e.name());
        this.error = e;
    }

}
