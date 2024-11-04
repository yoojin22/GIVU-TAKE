package com.accepted.givutake.global.exception;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ExceptionEnum error;

    public ApiException(ExceptionEnum e) {
        super(e.getMessage());
        this.error = e;
    }
}