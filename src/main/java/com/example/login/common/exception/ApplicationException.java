package com.example.login.common.exception;

import com.example.login.common.errors.ErrorCase;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ErrorCase exceptionCase;

    public ApplicationException(ErrorCase exceptionCase) {
        super(exceptionCase.getMessage());
        this.exceptionCase = exceptionCase;
    }

}