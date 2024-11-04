package com.accepted.givutake.global.exception;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import lombok.Getter;

@Getter
public class UnexpectedDataException extends RuntimeException {

    private final ExceptionEnum error;

    public UnexpectedDataException(ExceptionEnum e) {
        super(e.getMessage());
        this.error = e;
    }

}